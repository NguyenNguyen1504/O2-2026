/** 
  * The "armlet" architecture.
  *
  * @author Petteri Kaski <petteri.kaski@aalto.fi>
  *
  */

// DISASSEMBLER
package armlet:

  def decode(inst: Int,
             imm: Int = -1,
             hold: Boolean = false) =
     val opcode = inst & 0x3F
     val l = (inst >> 6) & 0x7
     val a = (inst >> 9) & 0x7
     val b = (inst >> 12) & 0x7
     val (mnem, operands, layout) = opcode_to_descr(opcode)
     var imm_s = "[defunct]";
     if layout.contains('I') then
       imm_s = if hold then "[imm]" else "%d".format(imm)
     layout match 
       case "N"   => "%s".format(mnem)
       case "LA"  => "%s $%d, $%d".format(mnem, l, a)
       case "LAB" => "%s $%d, $%d, $%d".format(mnem, l, a, b)
       case "AB"  => "%s $%d, $%d".format(mnem, a, b)
       case "A"   => "%s $%d".format(mnem, a)
       case "LI"  => "%s $%d, %s".format(mnem, l, imm_s)
       case "LAI" => "%s $%d, $%d, %s".format(mnem, l, a, imm_s) 
       case "AI"  => "%s $%d, %s".format(mnem, a, imm_s)
       case "I"   => "%s %s".format(mnem, imm_s)
       case _     => "[defunct]"
  end decode

  def disassemble(code: Seq[Int]) =
    var i = 0
    var b = new StringBuilder()
    val m = code.length
    while i < m do
      val inst = code(i)
      val opcode = inst & 0x3F
      val (mnem, operands, layout) = opcode_to_descr(opcode)
      if layout.contains('I') then
        if i + 1 < m then
          b.append("%05d: %s %s\n%05d: %s %s\n".format(
                   i,
                   intToString(inst),
                   decode(inst, code(i + 1)),
                   i+1,
                   intToString(code(i + 1)),
                   "[operand: %d]".format(code(i + 1),code(i + 1))))
        else
          b.append("%05d: %s %s\n".format(i,
                                            intToString(inst),
                                            decode(inst, -1, true)))
        i += 2
      else
        b.append("%05d: %s %s\n".format(i,intToString(inst),decode(inst, -1)))
        i += 1
    b.toString
  end disassemble
