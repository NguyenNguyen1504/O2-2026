/** 
  * The "armlet" architecture.
  *
  * @author Petteri Kaski <petteri.kaski@aalto.fi>
  *
  */

package armlet:
  // ASSEMBLER

  import collection.mutable.{Buffer,Set}
  import util.Random
  import util.parsing.combinator._     // for the assembler
  import util.parsing.input._          // ... to get positional info

  class Checker():
    val errs = Buffer[(Position,String)]()
    val syms = Set[String]()
    def report(p: Positional, m: String) = { errs += ((p.pos,m)) }
    def isDef(s: String) = syms.contains(s)
    def defSym(s: String) = { syms += s }
    def pass = errs.isEmpty
    def msgs = errs.sortWith( // "Position" has no sortBy support!?!
     (x:(Position,String),y:(Position,String)) => x._1 < y._1).map({ 
       case(p,m) => 
        "[%d.%d] error: %s\n%s\n".format(p.line, p.column, m, p.longString)
    }).foldLeft("")(_ ++ _)
  end Checker

  abstract class Elem:
    def address(a: Int, t: Map[String,Int]) = t
    def size = 0
    def assign(t: Map[String,Int]) = List(this)
    def binary = { require(false); 0 }
  end Elem

  class DataElem(d: Int) extends Elem:
    override def size = 1
    override def binary = d
  end DataElem

  class LabElem(s: String) extends Elem:
    override def address(a: Int, t: Map[String,Int]) = t + (s -> a)
    override def assign(t: Map[String,Int]) = List[Elem]()
  end LabElem

  class RefElem(s: String) extends Elem:
    override def size = 1
    override def assign(t: Map[String,Int]) = List(new DataElem(t(s)))
  end RefElem

  def ckInt(d: String): Boolean =
    val dd = if d.length > 2 && (d.take(2) == "0x" || d.take(2) == "0X") then
      BigInt(d.drop(2),16)
    else
      BigInt(d)
    // wordlength-based magic constants
    (dd <= BigInt(65535)) && (dd >= BigInt(-32768)) 
  end ckInt

  def toInt(d: String): Int =
    val dd = if d.length > 2 && (d.take(2) == "0x" || d.take(2) == "0X") then
      BigInt(d.drop(2),16)
    else
      BigInt(d)
    dd.toInt
  end toInt

  abstract class Operand extends Positional:
    def toElem(): Elem = { require(false); null }
    def rno: Int = { require(false); 0 }
    def signature: String
    def defCheck(c: Checker) : Unit
    def useCheck(c: Checker) : Unit
  end Operand

  case class Reg(no: Int) extends Operand:
    override def toString() = { "$%d".format(no) }
    override def rno = no
    def signature = "R"
    def defCheck(c: Checker) = { }
    def useCheck(c: Checker) = { }
  end Reg

  case class Imm(d: String) extends Operand:
    override def toString() = { "%s".format(d) }
    override def toElem() = { new DataElem(toInt(d)) }
    def signature = "I"
    def defCheck(c: Checker) =
      if !ckInt(d) then 
        c.report(this, 
                 "integer operand \"%s\" exceeds word length".format(d))
    def useCheck(c: Checker) = { }
  end Imm

  case class Ref(lab: String) extends Operand:
    override def toString() = { ">%s\n".format(lab) }
    override def toElem() = { new RefElem(lab) }
    def signature = "I"
    def defCheck(c: Checker) = { }
    def useCheck(c: Checker) = 
      if !c.isDef(lab) then
        c.report(this,
                 "reference to an undefined label \"%s\"".format(lab))
  end Ref

  def genN(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p))

  def genLA(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p | (o(0).rno << 6) | (o(1).rno << 9)))

  def genLAB(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p | (o(0).rno << 6) | (o(1).rno << 9) | (o(2).rno << 12)))

  def genAB(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p | (o(0).rno << 9) | (o(1).rno << 12)))

  def genA(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p | (o(0).rno << 9)))

  def genLI(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p | (o(0).rno << 6)), o(1).toElem())

  def genLAI(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p | (o(0).rno << 6) | (o(1).rno << 9)), o(2).toElem())

  def genAI(p: Int, o: List[Operand]): List[Elem] = 
    List(new DataElem(p | (o(0).rno << 9)), o(1).toElem())

  def genI(p: Int, o: List[Operand]) = 
    List(new DataElem(p), o(0).toElem())

  val layout_to_gen = 
    Map[String,(Int,List[Operand])=>List[Elem]]("N"   -> genN,
                                                "LA"  -> genLA,
                                                "LAB" -> genLAB,
                                                "AB"  -> genAB,
                                                "A"   -> genA,
                                                "LI"  -> genLI,
                                                "LAI" -> genLAI,
                                                "AI"  -> genAI,
                                                "I"   -> genI)

  abstract class Statement extends Positional:
    def defCheck(c: Checker) : Unit
    def useCheck(c: Checker) : Unit
    def generate(t: List[Elem]): List[Elem]
  end Statement

  class Instruction(mnem: String, ops: List[Operand]) extends Statement:
    override def toString() =
      "%s ".format(mnem) ++ {
        if ops.isEmpty then "(with no operands)"
        else
        // Orig: ops.head.toString ++ ops.tail.flatMap(o => ", " ++ o.toString())
        ops.tail.foldLeft(ops.head.toString)((acc : String,o) => acc ++ o.toString)
      }
    def defCheck(c: Checker) = 
      val sig = ops.map(_.signature).foldLeft("")(_ ++ _)
      if !type_to_opcode.isDefinedAt((mnem,if sig.length==0 then "N" else sig)) then
        c.report(this,
                 "invalid instruction \"%s\"".format(toString))
      ops.foreach(_.defCheck(c))
    def useCheck(c: Checker) = 
      ops.foreach(_.useCheck(c))
    def generate(tail: List[Elem]) = 
      val sig = ops.map(_.signature).foldLeft("")(_ ++ _)
      val p = type_to_opcode((mnem,if sig.length==0 then "N" else sig))
      val layout = opcode_to_descr(p)._3
      val gen = layout_to_gen(layout)
      gen(p, ops).foldRight(tail)(_ :: _)
  end Instruction

  class DataList(dd: List[String]) extends Statement:
    def defCheck(c: Checker) = 
      dd.foreach(d => if !ckInt(d) then { 
        c.report(this, "data value \"%s\" exceeds word length".format(d)) 
      } )
    def useCheck(c: Checker) = { }
    def generate(t: List[Elem]) = 
      dd.map(s => new DataElem(toInt(s))) .foldRight(t)(_ :: _) 
  end DataList

  class DataRand(dd: List[String]) extends Statement:
    def defCheck(c: Checker) = 
      if dd.length < 2 then
        c.report(this, "random seed and/or length missing")
    def useCheck(c: Checker) = { }
    def generate(t: List[Elem]) = 
      val dda = dd.toArray
      val seed = dda(0).toInt
      val len = dda(1).toInt
      val ub = if dda.length > 2 then dda(2).toInt else 65536
      val rnd = new Random(seed)
      (0 until len)
         .map(j => new DataElem(rnd.nextInt(ub)))
         .toList.foldRight(t)(_ :: _)
  end DataRand

  class DataRandPerm(dd: List[String]) extends Statement:
    def defCheck(c: Checker) = 
      if dd.length < 2 then
        c.report(this, "random seed and/or length missing")
    def useCheck(c: Checker) = { }
    def generate(t: List[Elem]) = 
      val dda = dd.toArray
      val seed = dda(0).toInt
      val len = dda(1).toInt
      val rnd = new Random(seed)
      rnd.shuffle(1 to len).map(j => new DataElem(j)).toList .foldRight(t)(_ :: _)
  end DataRandPerm

  abstract class Component extends Positional:
    def defCheck(c: Checker) : Unit
    def useCheck(c: Checker) : Unit
    def generate(t: List[Elem]): List[Elem]
  end Component

  class Label(s: String) extends Component:
    def defCheck(c: Checker) = 
      if c.isDef(s) then
        c.report(this, "label \"@%s\" redefined".format(s))
      else
        c.defSym(s)
    def useCheck(c: Checker) = { }
    def generate(t: List[Elem]) = { (new LabElem(s)) :: t }
  end Label

  class Block(stmts: List[Statement]) extends Component:
    def defCheck(c: Checker) = { stmts.map(_.defCheck(c)) }
    def useCheck(c: Checker) = { stmts.map(_.useCheck(c)) }
    def generate(t: List[Elem]) = { stmts.foldRight(t) { (s,t) => s.generate(t) } }
  end Block

  class Program(comps: List[Component]) extends Positional:
    def defCheck(c: Checker) = { comps.map(_.defCheck(c)) }
    def useCheck(c: Checker) = { comps.map(_.useCheck(c)) }
    def generate() = { comps.foldRight(List[Elem]()) { (c,t) => c.generate(t) } }
  end Program

  object armletAsmParser extends RegexParsers:
    override val whiteSpace = """(\s|#.*)+""".r
    val lab                 = "\\@[a-zA-Z0-9_]+:".r
    val ref                 = ">[a-zA-Z0-9_]+".r
    val numeric             = "(0[xX][0-9a-fA-F]+)|(-?[0-9]+)".r
    val mnemonic            = "[a-zA-Z0-9]+".r
    val reg                 = "\\$[0-7]".r

    def program: Parser[Program] =
      complist ^^ { case r => new Program(r) }
    def complist: Parser[List[Component]] = 
      positioned(component) ~ opt(complist) ^^ { case r ~ None => List(r)
                                                 case r ~ Some(s) => r :: s }
    def component: Parser[Component] = 
      ( lab               ^^ { case r => new Label(r.drop(1).dropRight(1)) }
      | block             ^^ { case r => r }
      )
    def block: Parser[Block] = 
      stmtlist ^^ { case r => new Block(r) }
    def stmtlist: Parser[List[Statement]] = 
      statement ~ opt(stmtlist) ^^ { case r ~ None => List(r)
                                     case r ~ Some(s) => r :: s }
    def statement: Parser[Statement] = 
      ( positioned(instruction) ^^ { case r => r } 
      | positioned(data)        ^^ { case r => r }
      )
    def instruction: Parser[Instruction] = 
      mnemonic ~ opt(operandlist) ^^ 
          { case r ~ None => new Instruction(r, List[Operand]())
            case r ~ Some(s) => new Instruction(r, s) }
    def operandlist: Parser[List[Operand]] = 
      positioned(operand) ~ opt("," ~> operandlist) ^^ 
          { case r ~ None    => List(r)
            case r ~ Some(s) => r :: s }
    def operand: Parser[Operand] = 
      ( reg         ^^ { case r => new Reg(r.drop(1).toInt) }
      | numeric     ^^ { case r => new Imm(r) }
      | ref         ^^ { case r => new Ref(r.drop(1)) }
      )          
    def data: Parser[Statement] = 
      ( "%data" ~> datalist ^^ { case r => new DataList(r) }
      | "%rand" ~> datalist ^^ { case r => new DataRand(r) }
      | "%randperm" ~> datalist ^^ { case r => new DataRandPerm(r) }
      )
    def datalist: Parser[List[String]] = 
      numeric ~ opt("," ~> datalist) ^^ 
                         { case r ~ None    => List(r)
                           case r ~ Some(s) => r :: s }

    def go(in: String): (Option[Program], String) =
      parseAll(program, in) match
        case Success(prg, in) => 
          (Option(prg), "parse successful")
        case f => 
          (None, "%s\n".format(f.toString))
  end armletAsmParser

  def assemble(in: String): (Option[Seq[Int]], String) =
    armletAsmParser.go(in) match
      case (Some(prg),str) => 
        val c = new Checker()
        prg.defCheck(c)
        prg.useCheck(c)
        if c.pass then       
          val elms = prg.generate()
          val atab = (elms zip 
            (elms.map(_.size).scanLeft(0)(_ + _).dropRight(1)))
            .foldLeft(Map[String,Int]()) {
              (t,ei) => ei._1.address(ei._2,t)
            }
          val ae = elms.map(_.assign(atab)) 
          val data = ae.foldRight(List[Elem]()){ (es,t) => es.foldRight(t)(_ :: _) }
          if data.length > 65536 then
            (None, 
             "binary (%d words) does not fit in armlet memory"
                .format(data.length)) 
          else
            (Option(data.map(_.binary)), "assembly successful")
        else
          (None, c.msgs)
      case (None, str) =>
        (None, str)
  end assemble

  def assembleEmptyFail(in: String): Seq[Int] =
     assemble(in) match
       case (Some(bin),str) => bin
       case (None,str) => List[Int]()
  end assembleEmptyFail

