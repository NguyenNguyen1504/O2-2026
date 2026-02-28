/*
* 
* This file is part of the CS-A1120 Programming 2 course materials at
* Aalto University in Spring 2026, and is for your personal use on that
* course only.
* Distribution of any parts of this file in any form, including posting or
* sharing on public or shared forums or repositories is *prohibited* and
* constitutes a violation of the code of conduct of the course.
* The programming exercises of CS-A1120 are individual and confidential
* assignments---this means that as a student taking the course you are
* allowed to individually and confidentially work with the material,
* to discuss and review the material with course staff, and submit the
* material for grading on course infrastructure.
* All other use - including, having other persons or programs
* (e.g. AI/LLM tools) working on or solving the exercises for you is
* forbidden, and constitutes a violation of the code of conduct of this
* course.
* 
*/


package qoi

import scala.swing._
import scala.swing.GridBagPanel.Fill
import java.awt.{Image, Dimension, Color}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.ImageIcon

/**
 * A simple app that lets you view images decoded by your qoi decoder
 * side-by-side with a refrence image
 * @author Juho Poteri
 */

object ImageViewer extends SwingApplication:
  def startup(args: Array[String]): Unit =
    println("Starting the image viewer")

  val imgPath = "a07-qoi/data/"
  val mediumImgBaseNames = Array(
    "checkerboard",
    "ultra_mango",
    "white_noise",
    "floating_orb",
    "Aalto_logo"
  )
  val largeImgBaseNames = Array(
    "city_LARGE",
    "shore_LARGE"
  )
  // Original images of "city" and "shore" by Iiri Poteri
  // Obtained for educational purposes on CS-A1120
  
  val textFont = new Font("Courier", java.awt.Font.BOLD, 20)
  
  val (pictureDim, marginal) = (512, 50)

  // add the large zoomable images here so that the fast zoom control can
  // toggle them
  private val largeImages = scala.collection.mutable.Buffer[ZoomableImage]()

  private lazy val headers = Seq(
    new TextField("Reference image from .png"):
      editable = false
      horizontalAlignment = Alignment.Center
      font = textFont
      background = new Color(255, 255, 255)
      border = Swing.MatteBorder(2, 2, 2, 1, Color.darkGray)
    ,
    new TextField("Image decoded using your qoi decoder"):
      editable = false
      horizontalAlignment = Alignment.Center
      font = textFont
      background = new Color(255, 255, 255)
      border = Swing.MatteBorder(2, 1, 2, 2, Color.darkGray)
  )

  // Creates the view of medium images
  private def mediumImgView =
    new ScrollPane:    // Images side-by-side in a scrollable pane
      contents = new GridPanel(mediumImgBaseNames.length, 2):
        contents ++= mediumImgBaseNames.flatMap({case imgName: String =>
            Seq(
              new Label:
                icon = new ImageIcon(imgPath + imgName + ".png")
                tooltip = imgName + ".png"
                border = Swing.CompoundBorder(
                  Swing.MatteBorder(2, 0, 2, 1, Color.darkGray),
                  Swing.EmptyBorder(2)
                )
              ,
              new Label:
                readQoiFileToImage(imgPath + imgName + ".qoi") match
                  case Some(img) =>
                    icon = new ImageIcon(img)
                    tooltip = imgName + ".qoi"
                  case None =>
                    text = "Could not successfully read and decode " + imgPath + imgName + ".qoi"
                border = Swing.CompoundBorder(
                  Swing.MatteBorder(2, 1, 2, 0, Color.darkGray),
                  Swing.EmptyBorder(2)
                )
            )
        })
        border = Swing.MatteBorder(1, 2, 2, 2, Color.black)
      columnHeaderView = new GridPanel(1, 2):   // "Headers" for both image columns
        contents ++= headers
      verticalScrollBar.unitIncrement = 12
  end mediumImgView

  private lazy val mediumViewSize = new Dimension(pictureDim * 2 + marginal, (pictureDim * 1.5).toInt)

  private def largeImgView(imgName: String): Component =
    largeImages.clear()   // Empty the previous ones
    val refImg = ImageIO.read(new java.io.File(imgPath + imgName + ".png"))
    val refImgComp = new ZoomableImage(refImg, fastZoomControl.selected)
    largeImages += refImgComp
    refImgComp.tooltip = imgName + ".png"
    refImgComp.border = Swing.CompoundBorder(
      Swing.MatteBorder(2, 0, 2, 1, Color.darkGray),
      Swing.EmptyBorder(2)
    )
    val qoiImgComp = readQoiFileToImage(imgPath + imgName + ".qoi") match
      case Some(img) =>
        val zImg = new ZoomableImage(img, fastZoomControl.selected):
          tooltip = imgName + ".qoi"
        largeImages += zImg
        zImg
      case None =>
        new Label("Could not successfully read and decode " + imgPath + imgName + ".qoi")
    qoiImgComp.border = Swing.CompoundBorder(
      Swing.MatteBorder(2, 1, 2, 0, Color.darkGray),
      Swing.EmptyBorder(2)
    )
    new GridBagPanel:
      val c = new Constraints
      c.gridy = 0
      c.gridx = 0
      c.gridheight = 1
      c.gridwidth = 1
      c.weighty = 0.0
      c.weightx = 1.0
      c.fill = Fill.Both
      layout(headers(0)) = c
      c.gridy = 0
      c.gridx = 1
      c.gridheight = 1
      c.gridwidth = 1
      c.weighty = 0.0
      c.weightx = 1.0
      layout(headers(1)) = c
      c.gridy = 1
      c.gridx = 0
      c.gridheight = 1
      c.gridwidth = 1
      c.weighty = 1.0
      c.weightx = 1.0
      layout(refImgComp) = c
      c.gridy = 1
      c.gridx = 1
      c.gridheight = 1
      c.gridwidth = 1
      c.weighty = 1.0
      c.weightx = 1.0
      layout(qoiImgComp) = c
  end largeImgView

  lazy val fastZoomControl = new CheckMenuItem("Fast zoom"):
    selected = true
    action = Action("Fast zoom"){
      //println(s"Fast zoom selected: $selected")
      largeImages.foreach(zoomableImg => zoomableImg.fastZoom = selected)
    }

  val window: MainFrame = new MainFrame:
    title = "Compare images"
    contents = mediumImgView
    size = mediumViewSize
    menuBar = new MenuBar:
      contents ++= Seq(
        new Menu("File"):
          contents += new MenuItem("Quit"):
            action = Action("Quit"){
              callQuit()
            }
        ,
        new Menu("Options"):
          contents += fastZoomControl
        ,
        new Menu("Switch images"):
          contents ++= Seq(
            new MenuItem("Medium images"):
              action = Action("Medium images"){
                window.contents = mediumImgView
                if window.size.width < mediumViewSize.width then
                  window.size = mediumViewSize
              }
            ,
            new Menu("Large images"):
              contents ++= largeImgBaseNames
                .map( imgName =>
                  new MenuItem(imgName):
                    action = Action(imgName){
                      window.contents = largeImgView(imgName)
                      window.maximize()
                    }
                )
          )
      )
    centerOnScreen()
    visible = true

  //def top = this.window

  def callQuit(): Unit = this.quit()

end ImageViewer
