/** 
  * The "armlet" architecture.
  *
  * @author Petteri Kaski <petteri.kaski@aalto.fi>
  *
  */

package armlet
// CODE FOR PLAYING WITH THE ARMLET ARCHITECTURE

import minilog.*

import collection.mutable.Buffer
import scala.swing._
import scala.swing.event._
import GridBagPanel._
import java.util.Calendar
import java.text.SimpleDateFormat
import java.net.URI
import java.io.{File,PrintWriter,FileReader,BufferedReader}
import javax.swing.text.JTextComponent
import javax.swing.{JPopupMenu,AbstractAction,ImageIcon}
import java.awt.{Cursor,Toolkit,Desktop}
import java.awt.datatransfer.{UnsupportedFlavorException,StringSelection,
                              Clipboard,ClipboardOwner,Transferable,DataFlavor}
import java.awt.event.{MouseEvent,MouseAdapter,ActionEvent,InputEvent}


// Runs the ALU on Trigger

class ALUTrigger():

  // Build it and run it on Trigger

  val c = new HookedCircuit()
  val (regs, immed_in, to_trigger) = armlet.buildPlainALU(c)

  // Go trigger ...

  val t = new Trigger(c)
  (0 until armlet.num_regs).foreach(i => 
    t.watch("$%d".format(i),
            regs(i).reverse,
            Trigger.intDecoder(regs(i))))
  to_trigger.foreach(x => t.watch(x._1,x._2,x._3))
  t.watch("immed_in",
          immed_in.reverse,
          Trigger.intDecoder(immed_in))
  t.go()
end ALUTrigger

// Runs the data path on Trigger

class DataPathTrigger():

  // Helper functions for Trigger: 
  // Instruction decoder and encoder for user input

  val instrDecoder = () => {
    armlet.decode(armlet.bitsToInt(instr_in.values),
                  armlet.bitsToInt(immed_in.values))
  }

  def instrEncoder(s: String) =
    val bin = armlet.assembleEmptyFail(s)
    if bin.length < 1 then
      false
    else
      (instr_in zip armlet.intToBits(bin(0))).foreach(x => x._1.set(x._2))
      if bin.length > 1 then
        (immed_in zip armlet.intToBits(bin(1))).foreach(x => x._1.set(x._2))
      else
        (immed_in zip armlet.intToBits(0)).foreach(x => x._1.set(x._2))
      true

  // Build it and run it on Trigger

  val c = new HookedCircuit()
  val read_in  = c.inputs(armlet.wordlength)
  val instr_in = c.inputs(armlet.wordlength)
  val immed_in = c.inputs(armlet.wordlength)

  val (regs, mem_read_e, mem_write_e, mem_addr, mem_data) = 
    armlet.buildDataPath(read_in,
                         instr_in,
                         immed_in)
  val mem = armlet.buildMemory(mem_read_e, 
                               mem_write_e, 
                               mem_addr, 
                               mem_data, 
                               read_in)

  // Go trigger ...

  val t = new Trigger(c)
  t.watch("instr_in", instr_in.reverse, instrDecoder, instrEncoder)
  t.watch("immed_in", immed_in.reverse)
  (0 until armlet.num_regs).foreach(i => 
    t.watch("$%d".format(i),
                  regs(i).reverse,
                  Trigger.intDecoder(regs(i))))
  t.go()
end DataPathTrigger

// A class for a "packaged" armlet processor & memory

class armletPackage():

  // Build the processor & memory

  val processor = new HookedCircuit()
  val reset_e = processor.input()
  val read_in = processor.inputs(armlet.wordlength)
  val (hlt_e, trp_e, mem_read_e, mem_write_e, mem_addr, mem_data, statusString)
    = armlet.buildProcessor(reset_e, read_in)
  val mem = armlet.buildMemory(mem_read_e, mem_write_e, mem_addr, mem_data, read_in)

  // Count the ticks here

  var ticks = 0L

  // Forward clock to processor & update ticks
 
  def clock() = 
    processor.clock() 
    ticks += 1

  // Binary loader interface

  def loadBinAndReset(bin: Seq[Int]) =

    // Load binary to memory

    (0 until mem.length).foreach(mem(_) = 0) // memory init to all zero
    mem(0) = 63 // ... but put a halt to word 0
    (0 until bin.length).foreach(i => mem(i) = bin(i)) // load binary

    // Reset processor (set reset high & run one clock)

    reset_e.set(true)
    processor.clock()
    reset_e.set(false) // ... and we are off to the races ...

    ticks = 0L

  def status = "tick = %d\n\n".format(ticks) ++ statusString()

end armletPackage


// Quick-and-dirty "Ticker" interface to a full armlet processor & memory


/*
 * Construct a Ticker object with an optional starting text and
 * a optional filename. The program will be saved upon exit to
 * the specified file.
 */
class Ticker(val startText: String = "",
             val filename: String = "") extends SwingApplication:
  val defaultStartText = 
    "#\n# Ready for your code over here\n#\n\n@origin:\n\tnop\n"

  var saveFile = if filename.isEmpty then new File(defaultFilename())
                 else new File(filename)

  val armletBox = new armletPackage()

  val fontC = new Font("Courier", java.awt.Font.PLAIN, 13)

  val prgtext = new TextArea:
    background = new Color(250, 250, 250)
    text = if startText.isEmpty then defaultStartText else startText
    columns = 58
    rows = 15
    font = fontC
  prgtext.listenTo(prgtext.mouse.clicks)
  prgtext.listenTo(prgtext.keys)
  prgtext.reactions += {
    case e: MouseClicked if (e.peer.getButton == MouseEvent.BUTTON3) =>
      val popup = new JPopupMenu
      popup.add(new AbstractAction("Cut") {
        override def actionPerformed(ae: ActionEvent) = { prgtext.cut() }
      })
      popup.add(new AbstractAction("Copy") {
        override def actionPerformed(ae: ActionEvent) = { prgtext.copy() }
      })
      popup.add(new AbstractAction("Paste") {
        override def actionPerformed(ae: ActionEvent) = { prgtext.paste() }
      })
      var nx = e.peer.getX()
      popup.show(e.peer.getComponent(), 
                 nx, 
                 e.peer.getY() - popup.getSize().height)
    case c: KeyReleased =>
      if c.key == Key.F12 then chooseSaveFile()
      if c.peer.isControlDown() then
        c.key match
          case Key.N => newFile()
          case Key.O => openFromFile()
          case Key.S => saveToFile()
          case _ =>
  }
  val bintext = new TextArea:
    background = new Color(250, 250, 250)
    editable = false
    text = ";; no binary available\n;; -- press \"Assemble\" to compile source"
                 //           1         2         3         4
                 // 01234567890123456789012345678901234567890
                 // 00000: 0101010101010101 mov $0, $1, 65535
    columns = 42
    rows = 15
    font = fontC
  val consoletext = new TextArea:
    background = new Color(250, 250, 250)
    editable = false
    text = "%s: \"Ticker\" ready\n".format(Calendar.getInstance().getTime())
    font = fontC
    columns = 100
    rows = 10
  val clearbutt = new Button:
    text = "Clear console"
    font = fontC
  val assemblebutt = new Button:
    text = ">> Assemble >>"
    font = fontC
  val loadbutt = new Button:
    text = ">> Load >>"
    font = fontC
    enabled = false
  val prgbin = new SplitPane(Orientation.Vertical, 
                             new ScrollPane(prgtext), 
                             new ScrollPane(bintext)) {
  }
  val consolecontrol = new GridBagPanel:
    val c = new Constraints
    c.gridx = 0
    c.gridy = 0
    c.gridwidth = 3
    c.gridheight = 5
    c.weighty = 1
    c.weightx = 1
    c.fill = Fill.Both
    layout(new ScrollPane(consoletext)) = c
    c.gridy += 5
    c.gridwidth = 1
    c.gridheight = 1
    c.weighty = 0
    c.weightx = 1
    c.fill = Fill.Horizontal
    layout(clearbutt) = c
    c.gridx += 1
    layout(assemblebutt) = c
    c.gridx += 1
    layout(loadbutt) = c
  val asm = new SplitPane(Orientation.Horizontal, 
                          prgbin, 
                          consolecontrol) { }
  val regtext = new TextArea:
    background = new Color(250, 250, 250)
    editable = false
    text = ""
    columns = 45
    rows = 10
    font = fontC
  val memtext = new ListView(armletBox.mem):
    background = new Color(250, 250, 250)
    enabled = false
    font = fontC
    renderer = ListView.Renderer(d => 
      "%s 0x%04X %5d %s".format(armlet.intToString(d),
                                d,
                                d,
                                armlet.decode(d, -1, true)))
  val runbutt = new Button:
    text = ">>> Run until halt/trap >>>"
    font = fontC
    enabled = false
  val stopbutt = new Button:
    text = "[[ Stop ]]"
    font = fontC 
    enabled = true
  val tickbutt = new Button:
    text = "> One clock tick >"
    font = fontC
    enabled = false
  val regmem = new SplitPane(Orientation.Vertical, 
                             new ScrollPane(regtext), 
                             new ScrollPane(memtext)) {
  }
  val arm = new GridBagPanel:
    val c = new Constraints
    c.gridx = 0
    c.gridy = 0
    c.gridwidth = 3
    c.gridheight = 5
    c.weighty = 1
    c.weightx = 1
    c.fill = Fill.Both
    layout(new ScrollPane(regmem)) = c
    c.gridy += 5
    c.gridwidth = 1
    c.gridheight = 1
    c.weighty = 0
    c.weightx = 1
    c.fill = Fill.Horizontal
    layout(runbutt) = c
    c.gridx += 1
    layout(stopbutt) = c
    c.gridx += 1
    layout(tickbutt) = c

  val tabp = new TabbedPane:        
    pages += new TabbedPane.Page("Assembler/Loader", asm):
      font = fontC
    pages += new TabbedPane.Page("Processor/Memory", arm):
      font = fontC

  def updateStatus() =
    regtext.text = armletBox.status
    if armletBox.mem_read_e.value || armletBox.mem_write_e.value then
      val addr = armlet.bitsToInt(armletBox.mem_addr.values)
      memtext.selectIndices(addr)
      if armletBox.mem_read_e.value then // read
        memtext.selectionBackground = new Color(200, 200, 255)
      else // must be a write
        memtext.selectionBackground = new Color(255, 200, 200)
    else
        memtext.selectIndices() // no memory op

  var bin = Seq[Int]()

  @volatile var running = false
  var run_thread: Thread = null

  def top = new Frame { frame =>
    title = "Ticker: " + saveFile.getName()
    background = new Color(255, 255, 255)
    menuBar = new MenuBar:
      contents += new Menu("File"):
        contents += new MenuItem(Action("New        Ctrl+N")
          (newFile()))
        contents += new MenuItem(Action("Open      Ctrl+O")
          (openFromFile()))
        contents += new MenuItem(Action("Save       Ctrl+S")
          (saveToFile()))
        contents += new MenuItem(Action("Save As...   F12")
          (chooseSaveFile()))
        contents += new Separator()
        contents += new MenuItem(Action("Exit         Alt+F4")
          (closeOperation()))
      contents += new Menu("Help"):
        contents += new MenuItem(Action("Documentation")
          (displayDocumentation()))
    contents = tabp
    listenTo(clearbutt)
    listenTo(assemblebutt)
    listenTo(loadbutt)
    listenTo(runbutt)
    listenTo(stopbutt)
    listenTo(tickbutt)
    listenTo(loadbutt)
    reactions += {
      case ButtonClicked(b) =>
        if b == clearbutt then
          consoletext.text = ""
        if b == assemblebutt then
          val asmresult = armlet.assemble(prgtext.text)
          asmresult match
            case (Some(code),str) =>
              bin = code
              consoletext.text += "%s: assembly successful, binary created\n"
                                    .format(Calendar.getInstance().getTime())
              bintext.text = ";;\n;; binary image -- ready to load\n;;\n;; %s\n;;\n\n%s"
                               .format(Calendar.getInstance().getTime(),
                                       armlet.disassemble(code))
              loadbutt.enabled = true
            case (None,str) => 
              consoletext.text += str
              consoletext.text += "%s: assembly failure\n"
                                  .format(Calendar.getInstance().getTime())
        if b == loadbutt then
          armletBox.loadBinAndReset(bin)
          updateStatus()
          tabp.peer.setSelectedIndex(1) // to "Processor/Memory" page
          tickbutt.enabled = true
          runbutt.enabled = true
        if b == tickbutt then
          armletBox.clock()
          updateStatus()
        if b == runbutt then
          tickbutt.enabled = false
          loadbutt.enabled = false
          assemblebutt.enabled = false
          runbutt.enabled = false
          tabp.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
          run_thread = new Thread(new Runnable { 
            def run() = {
              running = true
              while { {
                armletBox.clock() // run armlet, run ...
              } ;!armletBox.hlt_e.value &&
                      !armletBox.trp_e.value && 
                      !Thread.interrupted()} do ()
              running = false
              updateStatus()
              memtext.repaint()
              tabp.cursor = Cursor.getDefaultCursor()
              tickbutt.enabled = true
              loadbutt.enabled = true
              assemblebutt.enabled = true
              runbutt.enabled = true
            }
          })
          run_thread.start()
        if b == stopbutt then
          if !running then
            tabp.peer.setSelectedIndex(0) // to "Assembler/Loader" page
          else
            run_thread.interrupt()
    }
    override def closeOperation() =
      visible = true
      val res = Dialog.showConfirmation(null, "Are you sure you want to exit?")
      if res == Dialog.Result.Ok then
        if running then
          run_thread.interrupt()
          run_thread.join()
        val res = Dialog
                .showConfirmation(null, 
                                  "Save the program to current file (%s)?"
                                    .format(saveFile.getName()))
        if res == Dialog.Result.Ok then saveToFile()
        dispose()
  }

  def openFromFile() =
    val chooser = new FileChooser(new File("."))
    chooser.title = "Choose file to open"
    val result = chooser.showOpenDialog(null)
    if result == FileChooser.Result.Approve then
      saveFile = chooser.selectedFile
      val reader = new BufferedReader(new FileReader(saveFile))
      val lines = new StringBuilder()
      var line = reader.readLine()
      while line != null do
        lines ++= line + "\n"
        line = reader.readLine()
      reader.close()
      prgtext.text = lines.mkString
      setTitle()
      consoletext.text += "%s: opened file %s\n"
                            .format(Calendar.getInstance().getTime(),
                                    saveFile.getName())

  def newFile() =
    val res = Dialog.showConfirmation(null, 
                                      "Save the program to current file (%s)?"
                                        .format(saveFile.getName()))
    if res == Dialog.Result.Ok then saveToFile()
    saveFile = new File(defaultFilename())
    prgtext.text = defaultStartText
    setTitle()

  def chooseSaveFile() =
    val chooser = new FileChooser(new File("."))
    chooser.title = "Choose file to save to"
    val result = chooser.showSaveDialog(null)
    if result == FileChooser.Result.Approve then
      saveFile = chooser.selectedFile
      saveToFile()
      setTitle()

  def saveToFile() =
    val pw = new PrintWriter(saveFile)
    pw.write(prgtext.text)
    pw.close()
    consoletext.text += "%s: saved to file %s\n"
                          .format(Calendar.getInstance().getTime(),
                                  saveFile.getName())

  def setTitle() : Unit =
    t.title = "Ticker: %s".format(saveFile.getName())

  def defaultFilename(): String =
    val now = Calendar.getInstance().getTime()
    val formatString = new SimpleDateFormat("MMddHHmm")
    "prog%s.s".format(formatString.format(now))

  def displayDocumentation() =
    val url = "https://a1120.cs.aalto.fi/2023/notes/round-a-programmable-computer--armlet-programming.html#our-first-armlet-program"
    if Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) then
      Desktop.getDesktop.browse(new URI(url))
    else
      Runtime.getRuntime().exec("xdg-open %s&".format(url))

  override def startup(args: Array[String]) = { }
  override def quit() = { }

  val t = top
  
  if t.size == new Dimension(0,0) then t.pack()
  t.centerOnScreen()
  t.visible = true


