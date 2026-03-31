/** 
  * The "armlet" architecture.
  *
  * @author Petteri Kaski <petteri.kaski@aalto.fi>
  *
  */

package armlet:

  import scala.collection.immutable
  import minilog._                     // ... it all runs on minilog


  // -------------------------
  // THE "ARMLET" ARCHITECTURE
  // -------------------------
  //
  // Processor design parameters
  // Note: changing these requires changes to
  //       the circuit design and the instruction set

  val num_regs        = 8     // number of general-purpose regs in processor
  val num_regs_log2   = 3     // ... base 2 log of the above
  val wordlength      = 16    // length of each word in bits

  //
  // PROCESSOR INSTRUCTION SET DESCRIPTION
  //
  // Each instruction is 16 bits in length (possibly followed by 16 bits
  // of immediate data), and consists of an opcode and additional information. 
  // The least significant 6 bits of an instruction specify the 6-bit
  // instruction opcode P, followed by additional information depending on
  // the layout of the instruction:
  //
  // 1111110000000000
  // 5432109876543210  (bit index)
  //
  // ????AAALLLPPPPPP  LA:   monadic operator                $L = op $A
  // ?BBBAAALLLPPPPPP  LAB:  dyadic operator                 $L = $A op $B
  // ???????LLLPPPPPP  LI:   monadic operator with imm data  $L = op I
  // ????AAALLLPPPPPP  LAI:  dyadic operator with imm data   $L = $A op I
  //
  // ??????????PPPPPP  N:    niladic control                 op
  // ????AAA???PPPPPP  A:    monadic control                 op $A
  // ?BBBAAA???PPPPPP  AB:   dyadic control                  $A op $B
  // ??????????PPPPPP  I:    monadic control with imm data   op I
  // ????AAA???PPPPPP  AI:   dyadic control with imm data    $A op I
  //
  // Here:
  //
  // P = 6-bit opcode (0,1,...,63)
  // L = lval (3-bit register identifier)
  // A = first rval (3-bit register identifier)
  // B = second rval (3-bit register identifier)
  // ? = [unused]
  //
  // The opcodes, their operand types, and operand layouts are as follows:
  //
  val opcodes = immutable.ArraySeq(

      // Instructions that take register operands (if any)

      (0,  "nop" , "N"   , "N"   ), // no operation
      (1,  "mov" , "RR"  , "LA"  ), // rval to lval
      (2,  "and" , "RRR" , "LAB" ), // bitwise AND of rvals to lval
      (3,  "ior" , "RRR" , "LAB" ), // bitwise IOR of rvals to lval
      (4,  "eor" , "RRR" , "LAB" ), // bitwise EOR of rvals to lval
      (5,  "not" , "RR"  , "LA"  ), // bitwise NOT of rval to lval
      (6,  "add" , "RRR" , "LAB" ), // sum of rvals to lval
      (7,  "sub" , "RRR" , "LAB" ), // difference of rvals to lval
      (8,  "neg" , "RR"  , "LA"  ), // negation of rval to lval
      (9,  "lsl" , "RRR" , "LAB" ), // left logical shift of $A by $B 
      (10, "lsr" , "RRR" , "LAB" ), // right logical shift of $A by $B 
      (11, "asr" , "RRR" , "LAB" ), // right arithmetic shift of $A by $B
      (12, "loa" , "RR"  , "LA"  ), // load from memory word pointed by rval
      (13, "sto" , "RR"  , "LA"  ), // store to memory word pointed by lval
      (14, "cmp" , "RR"  , "AB"  ), // compare vals, result to status
      (15, "jmp" , "R"   , "A"   ), // jump to rval
      (16, "beq" , "R"   , "A"   ), // ... if status is equal
      (17, "bne" , "R"   , "A"   ), // ... if status is not equal
      (18, "bgt" , "R"   , "A"   ), // ... if status is greater than
      (19, "blt" , "R"   , "A"   ), // ... if status is lesser than
      (20, "bge" , "R"   , "A"   ), // ... if status is greater than or equal
      (21, "ble" , "R"   , "A"   ), // ... if status is lesser than or equal
      (22, "bab" , "R"   , "A"   ), // ... if status is above
      (23, "bbw" , "R"   , "A"   ), // ... if status is below
      (24, "bae" , "R"   , "A"   ), // ... if status is above or equal
      (25, "bbe" , "R"   , "A"   ), // ... if status is below or equal

      // Instructions that take immediate data

      (26, "mov" , "RI"  , "LI"  ), // rval to lval
      (27, "and" , "RRI" , "LAI" ), // bitwise AND of rvals to lval
      (28, "ior" , "RRI" , "LAI" ), // bitwise IOR of rvals to lval
      (29, "eor" , "RRI" , "LAI" ), // bitwise EOR of rvals to lval
      (30, "add" , "RRI" , "LAI" ), // sum of rvals to lval
      (31, "sub" , "RRI" , "LAI" ), // difference of rvals to lval
      (32, "lsl" , "RRI" , "LAI" ), // left logical shift of $A by I
      (33, "lsr" , "RRI" , "LAI" ), // right logical shift of $A by I
      (34, "asr" , "RRI" , "LAI" ), // right arithmetic shift of $A by $B
      (35, "cmp" , "RI"  , "AI"  ), // compare rvals, result to status
      (36, "jmp" , "I"   , "I"   ), // jump to rval
      (37, "beq" , "I"   , "I"   ), // ... if status is equal
      (38, "bne" , "I"   , "I"   ), // ... if status is not equal
      (39, "bgt" , "I"   , "I"   ), // ... if status is greater than
      (40, "blt" , "I"   , "I"   ), // ... if status is lesser than
      (41, "bge" , "I"   , "I"   ), // ... if status is greater than or equal
      (42, "ble" , "I"   , "I"   ), // ... if status is lesser than or equal
      (43, "bab" , "I"   , "I"   ), // ... if status is above
      (44, "bbw" , "I"   , "I"   ), // ... if status is below
      (45, "bae" , "I"   , "I"   ), // ... if status is above or equal
      (46, "bbe" , "I"   , "I"   ), // ... if status is below or equal

      // Unused opcodes and the processor halt 

      (47, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (48, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (49, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (50, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (51, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (52, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (53, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (54, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (55, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (56, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (57, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (58, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (59, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (60, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (61, "hlt" , "N"   , "N"   ), // [unused opcode -- will halt processor]
      (62, "trp" , "N"   , "N"   ), // processor trap
      (63, "hlt" , "N"   , "N"   )) // processor halt

  // Helper tables and functions to build instruction encoders/decoders

  val mnemonics           = opcodes.map(x => x._2).toSet.toList
  val opcode_to_descr     = opcodes.map(x => (x._1,(x._2,x._3,x._4))).toMap
  val type_to_opcode      = opcodes.map(x => ((x._2,x._3),x._1)).toMap
  def layout_to_opcodes(l: String) = opcodes.filter(x => (x._4 == l)).map(_._1)
  def mnem_to_opcodes(m: String)   = opcodes.filter(x => (x._2 == m)).map(_._1)


  // BUILDER FUNCTIONS FOR BASIC BUS OPERATIONS

  def buildDecoder(aa: Bus): Bus = 
    aa.foldLeft(Bus(aa.host.True))((d,g) => (d && !g)++(d && g))

  def buildBusSelector(in: Seq[Bus], sel: Bus): Bus = 
    (in zip sel).map(x => x._1 && x._2).reduceLeft(_ | _)
 
  def buildAdd0(aa: Bus, bb: Bus, c0v: Boolean) =
    new Bus(
     ((aa zip bb).scanLeft((aa.host.False, 
                            if c0v then aa.host.True else aa.host.False))) {
       case ((s,c),(a,b)) => (a + b + c,(a && b)||(a && c)||(b && c))
     }.drop(1).map(_._1))
  def buildAdd(aa: Bus, bb: Bus) = buildAdd0(aa,bb,false)
  def buildSub(aa: Bus, bb: Bus) = buildAdd0(aa,~bb,true)
  def buildNeg(bb: Bus) = buildSub(bb.host.falses(bb.length),bb)
  def buildInc(bb: Bus) = buildAdd0(bb.host.falses(bb.length),bb,true)

  def buildEQ(aa: Bus, bb: Bus) = 
    !((aa zip bb).map(x => x._1 + x._2).reduceLeft(_ || _))
  def buildAB(aa: Bus, bb: Bus) = 
    (aa zip bb).foldRight(aa.host.False,aa.host.False) {
      case ((a,b),(d,q)) => (d || (a+b), (q && d)||(a && !b && !d))
    }._2
  def buildAE(aa: Bus, bb: Bus) = buildAB(aa, bb) || buildEQ(aa, bb)

  def buildRightS0(in: Bus, amount: Bus, arith: Boolean) =
    val w = in.length
    val b = (0 until w).filter(j => (1 << j) <= w).max
    val m = if (1 << b) < w then b + 1 else b // log2(w) rounded up
    val z = amount(m until w).reduceLeft(_ || _)
    def ls(in: Bus, j: Int) = 
      in(j until w) ++ 
      new Bus((0 until j).map(i => if arith then in(w-1) else in.host.False))
    amount(0 until m).zipWithIndex.foldLeft((in && !z)) { 
      case (b,(e,j)) => (b && !e) | (ls(b, 1 << j) && e)
    }
  def buildRightS(in: Bus, amount: Bus) = buildRightS0(in, amount, false)
  def buildARightS(in: Bus, amount: Bus) = buildRightS0(in, amount, true)
  def buildLeftS(in: Bus, amount: Bus) = 
    buildRightS0(in.reverse, amount, false).reverse


  //
  // SUBUNIT BUILDERS FOR THE DATA PATH
  //
  // The data path has three subunits:
  // 1. The load completion unit
  // 2. The arithmetic logic unit
  // 3. The memory interface unit
  //

  // A helper table of the arithmetic-and-logic operations and their 
  // operator-builder functions:

  val ops_ALU = immutable.ArraySeq(
      ("mov" , (a: Bus, b: Bus) => a),
      ("and" , (a: Bus, b: Bus) => a & b),
      ("ior" , (a: Bus, b: Bus) => a | b),
      ("eor" , (a: Bus, b: Bus) => a ^ b),
      ("not" , (a: Bus, b: Bus) => ~a),
      ("neg" , (a: Bus, b: Bus) => buildNeg(a)),
      ("add" , (a: Bus, b: Bus) => buildAdd(a,b)),
      ("sub" , (a: Bus, b: Bus) => buildSub(a,b)),
      ("lsl" , (a: Bus, b: Bus) => buildLeftS(a,b)),
      ("lsr" , (a: Bus, b: Bus) => buildRightS(a,b)),
      ("asr" , (a: Bus, b: Bus) => buildARightS(a,b)))

  // Load completion unit

  def buildLCU(reg_in:     Seq[Bus],
               read_in:    Bus,
               prev_l_idx: Bus,
               lcu_e:      Gate) =

    // Complete load if load enabled

    val l_select = buildDecoder(prev_l_idx) && lcu_e
    val reg_out = (reg_in zip 
                   l_select).map(x => ((x._1 && !x._2)|(read_in && x._2)))

    reg_out
  end buildLCU

  // Arithmetic logic unit

  def buildALU(reg_in:    Seq[Bus],
               l_idx:     Bus,
               a_idx:     Bus,
               b_idx:     Bus,
               immed_in:  Bus,
               imm_a_e:   Gate,
               imm_b_e:   Gate,
               opers:     Seq[(Gate,(Bus,Bus)=>Bus)],
               reset_e:   Gate) =

    // Decode and select $A
    val a_select = buildDecoder(a_idx)
    var ra = buildBusSelector(reg_in, a_select)

    // Decode and select $B
    val b_select = buildDecoder(b_idx)
    var rb = buildBusSelector(reg_in, b_select)

    // Left operand -- register or immediate?
    val opa = (ra && !imm_a_e) | (immed_in && imm_a_e)

    // Right operand -- register or immediate?
    val opb = (rb && !imm_b_e) | (immed_in && imm_b_e)
    
    // Run the operation builder function for each operation
    val results = opers.map(_._2(opa,opb))
    
    // Comparison of operands
    val cmp_sgn = buildSub(opa,opb)
    val f_eq    = !(opa ^ opb).reduceLeft(_ || _)     // opA == opB 
    val f_ab    = buildAB(opa,opb)                    // opA > opB  (unsigned)
    val f_gt    = !f_eq && !cmp_sgn(cmp_sgn.length-1) // opA > opB  (signed)

    // Select result-of-operation based on enable-signals for each operation
    val res = (results zip 
               opers.map(_._1)).map(x => x._1 && x._2).reduceLeft(_ | _)

    // Save result to register if there is an operation that is enabled
    val save_e = opers.map(_._1).reduceLeft(_ || _)
    val l_select = buildDecoder(l_idx) && save_e
      
    val reg_save = (reg_in zip 
                    l_select).map(x => ((x._1 && !x._2)|(res && x._2)))

    // Zap all regs on reset
    val reg_out = reg_save.map(_ && !reset_e)

    // Output new values of registers & flags
    (reg_out,
     f_eq,f_ab,f_gt,
     opa)
  end buildALU

  // Memory interface unit

  def buildMIU(reg_in:  Seq[Bus],
               l_idx:   Bus,
               a_idx:   Bus,
               loa_e:   Gate,
               sto_e:   Gate,
               pc_in:   Bus,
               ilo_e:   Gate,
               reset_e: Gate) =

    // Reset forces instruction load at pc_in

    val lor_e = loa_e && !reset_e
    val str_e = sto_e && !reset_e
    val ilr_e = ilo_e || reset_e

    // Decode and select $L

    val l_select = buildDecoder(l_idx)
    var rl = buildBusSelector(reg_in, l_select)

    // Decode and select $A

    val a_select = buildDecoder(a_idx)
    var ra = buildBusSelector(reg_in, a_select)

    // Feed the memory controller

    val mem_write_e = str_e
    val mem_read_e  = lor_e || ilr_e
    val alu_addr    = (ra && mem_read_e) | (rl && mem_write_e)
    val mem_addr    = (alu_addr && !ilr_e) | (pc_in && ilr_e)
    val mem_data    = ra

    // Feed the load completion & instruction loader units (for next cycle) 

    val lcu_e      = lor_e
    val prev_l_idx = l_idx
    val lsw_e      = lor_e || str_e

    val reg_out = reg_in   // Registers pass through 
                           // (load completion unit will complete load)

    (reg_out,prev_l_idx,lcu_e,lsw_e,
     mem_read_e,mem_write_e,mem_addr,mem_data)

  end buildMIU
  //
  // SUBUNIT BUILDERS FOR CONTROL AND EXECUTION
  //
  // Control and execution has three subunits:
  // 
  // 1. Instruction loader unit
  // 2. Instruction decoder unit   (-- this suffices to run the data path)
  // 3. Jump and branch unit
  //

  // 
  // A helper table of the jump-and-branch operations and 
  // their trigger-builder functions: 
  //              
  val ops_JBU = immutable.ArraySeq(
      ("jmp" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => f_eq.host.True),
      ("beq" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => f_eq),
      ("bne" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => !f_eq),
      ("bgt" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => f_gt),
      ("blt" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => !(f_eq || f_gt)),
      ("bge" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => f_eq || f_gt),
      ("ble" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => !f_gt),
      ("bab" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => f_ab),
      ("bbw" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => !(f_eq || f_ab)),
      ("bae" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => f_eq || f_ab),
      ("bbe" , (f_eq: Gate, f_ab: Gate, f_gt: Gate) => !f_ab))

  // Helper function to build enable sigs

  def buildDNFIntSeq(op: Bus, seq: Seq[Int]) =
    val bseq = seq.map(x => (0 until op.length).map(j => (x & (1<<j))!=0))
    bseq.map(t => (t zip op).map(x => 
       if x._1 then x._2 else !x._2).reduceLeft(_ && _)).reduceLeft(_ || _)
  end buildDNFIntSeq

  // Instruction loader unit 

  def buildILU(instr_in:   Bus,
               last_instr: Bus,
               oc_e:       Gate,
               lsw_e:      Gate,
               reset_e:    Gate) =

    // Do we really have anything to execute, 
    // or are we just waiting for a load/store to complete?

    val instr_lsw = (instr_in && !lsw_e) | 
                    (instr_in.host.falses(instr_in.length) && lsw_e)

    // Run operand completion if enabled 

    val instr     = (instr_lsw && !oc_e) | (last_instr && oc_e)
    val immed_in  = (instr.host.falses(instr.length) && !oc_e) | 
                    (instr_lsw && oc_e)

    // Do we have the complete instruction to execute, 
    // or do we issue a wait to load the next instruction?

    val opcode     = instr(0 until 6)
    val immed_e    = buildDNFIntSeq(opcode,
                                    layout_to_opcodes("LI") ++
                                    layout_to_opcodes("LAI") ++
                                    layout_to_opcodes("AI") ++
                                    layout_to_opcodes("I"))
    val wait_e     = immed_e && !oc_e && !reset_e
    val instr_exec = (instr && !wait_e) | 
                     (instr.host.falses(instr.length) && wait_e) 
    val instr_wait = instr

    (instr_exec, instr_wait, immed_in, wait_e)
  end buildILU

  // Instruction decoder unit 

  def buildIDU(instr_in: Bus,
               immed_in: Bus) =

    // Decode instruction 

    // Break instruction into parts 

    val opcode = instr_in(0 until 6)
    val l_idx  = instr_in(6 until 9)
    val a_idx  = instr_in(9 until 12)
    val b_idx  = instr_in(12 until 15)

    // Create enable signals for subunits based on opcode

    val imm_a_e  = buildDNFIntSeq(opcode,
                                  layout_to_opcodes("LI") ++
                                  layout_to_opcodes("I"))
      // load immediate data to left operand in ALU
    val imm_b_e  = buildDNFIntSeq(opcode,
                                  layout_to_opcodes("LAI") ++
                                  layout_to_opcodes("AI"))
      // load immediate data to right operand in ALU

    val ALU_es = ops_ALU.map(x => buildDNFIntSeq(opcode,mnem_to_opcodes(x._1)))
    val to_ALU_builder = ALU_es zip ops_ALU.map(_._2)
        // save result-of-operation to register?

    val loa_e = buildDNFIntSeq(opcode,mnem_to_opcodes("loa"))
    val sto_e = buildDNFIntSeq(opcode,mnem_to_opcodes("sto"))

    val cmp_e = buildDNFIntSeq(opcode,mnem_to_opcodes("cmp"))
    val JBU_es = ops_JBU.map(x => buildDNFIntSeq(opcode,mnem_to_opcodes(x._1)))
    val to_JBU_builder = JBU_es zip ops_JBU.map(_._2)

    val hlt_e = buildDNFIntSeq(opcode,mnem_to_opcodes("hlt"))
    val trp_e = buildDNFIntSeq(opcode,mnem_to_opcodes("trp"))

    (l_idx, a_idx, b_idx,
     immed_in, 
     imm_a_e, imm_b_e,
     to_ALU_builder,
     loa_e, sto_e,
     cmp_e,
     to_JBU_builder,
     hlt_e,
     trp_e)
  end buildIDU

  // Jump and branch unit

  def buildJBU(pc_in:     Bus,
               psr_in:    Bus,
               target:    Bus,
               cmp_e:     Gate,
               opers:     Seq[(Gate,(Gate,Gate,Gate)=>Gate)],
               f_eq_in:   Gate,
               f_ab_in:   Gate,
               f_gt_in:   Gate,
               hlt_e:     Gate,
               reset_e:   Gate) =

    val f_eq = psr_in(0)
    val f_ab = psr_in(1)
    val f_gt = psr_in(2)
    val results  = opers.map(_._2(f_eq,f_ab,f_gt))
    val do_jump  = (opers.map(_._1) zip 
                    results).map(x => x._1 && x._2).reduceLeft(_ || _)
    val pc_next  = (buildInc(pc_in) && !hlt_e) | 
                   (pc_in && hlt_e)
    val pc_save  = (pc_next && !do_jump) | (target && do_jump)
    val psr_save = (psr_in && !cmp_e) |
                   ((Bus(f_eq_in,f_ab_in,f_gt_in) ++ 
                     psr_in(3 until psr_in.length)) && cmp_e)   
    val pc_out   = pc_save  && !reset_e  
    val psr_out  = psr_save && !reset_e
 
    (pc_out, psr_out)
  end buildJBU