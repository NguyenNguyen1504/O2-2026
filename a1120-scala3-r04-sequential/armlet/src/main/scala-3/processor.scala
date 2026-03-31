/** 
  * The "armlet" architecture.
  *
  * @author Petteri Kaski <petteri.kaski@aalto.fi>
  *
  */

// PROCESSOR AND PARTIAL PROCESSOR BUILDERS

package armlet:
  import minilog.*
  // Full processor 

  def buildProcessor(reset_e: Gate,
                     read_in: Bus) =

    // Build processor internal state elements

    val host       = read_in.host
    val reg_in     = (0 until num_regs).map(x => host.inputs(wordlength))
    val pc_in      = host.inputs(wordlength)
    val psr_in     = host.inputs(wordlength)
    val last_instr = host.inputs(wordlength)
    val oc_e       = host.input()
    val lsw_e      = host.input()
    val lcu_e      = host.input()
    val prev_l_idx = host.inputs(num_regs_log2)
  
    // Build instruction loader unit

    val (instr_exec, instr_wait, immed_in, wait_e)
      = buildILU(read_in,last_instr,oc_e,lsw_e,reset_e)

    // Build instruction decode unit

    val (l_idx, a_idx, b_idx, i_in, ia_e, ib_e, to_ALU_builder,
         loa_e, sto_e, cmp_e, to_JBU_builder, hlt_e, trp_e)
      = buildIDU(instr_exec, immed_in)

    // Build load completion unit

    val reg_postLCU = buildLCU(reg_in, read_in, prev_l_idx, lcu_e)

    // Build arithmetic logic unit

    val (reg_postALU, f_eq, f_ab, f_gt, opA)
      = buildALU(reg_postLCU, l_idx, a_idx, b_idx, i_in,
                 ia_e, ib_e, to_ALU_builder, reset_e)

    // Build jump and branch unit

    val (pc_out, psr_out) = buildJBU(pc_in, psr_in, opA, cmp_e, to_JBU_builder,
                                     f_eq, f_ab, f_gt, hlt_e || lsw_e, reset_e)

    // Build memory interface unit

    val (reg_out, out_l_idx, out_lcu_e, out_lsw_e,
         mem_read_e, mem_write_e, mem_addr, mem_data)
      = buildMIU(reg_postALU, l_idx, a_idx, loa_e,
                 sto_e, pc_out, !(loa_e || sto_e), reset_e)

    // Build processor internal feedbacks

    (reg_in zip reg_out).map(x => x._1.buildFeedbackFrom(x._2))
    lcu_e.buildFeedbackFrom(out_lcu_e)
    lsw_e.buildFeedbackFrom(out_lsw_e)
    prev_l_idx.buildFeedbackFrom(out_l_idx)

    oc_e.buildFeedbackFrom(wait_e)
    last_instr.buildFeedbackFrom(instr_wait)
    pc_in.buildFeedbackFrom(pc_out)
    psr_in.buildFeedbackFrom(psr_out)                   

    // Status reporting for interactive single-step use

    def instrDecoder() =
      if !lsw_e.value then
        if wait_e.value then
          decode(bitsToInt(instr_wait.values),
                 -1,
                 true)
        else
          decode(bitsToInt(instr_exec.values),
                 bitsToInt(immed_in.values),
                 false)
      else
        "[loa/sto wait]"

    def statusString = () => { 
      "instr: %s\n".format(instrDecoder()) ++
      "pc:  %s\n".format(bitsToString(pc_in.values)) ++
      "psr: %s\n".format(bitsToString(psr_in.values)) ++
      (0 until num_regs).map(i => 
        "$%d:  %s\n".format(i,bitsToString(reg_postLCU(i).values))).foldLeft("")(_ ++ _) ++
      "\n" ++
      "mem_read_e:  %d\n".format(if mem_read_e.value then 1 else 0) ++
      "mem_write_e: %d\n".format(if mem_write_e.value then 1 else 0) ++
      "mem_addr:    %s\n".format(bitsToString(mem_addr.values)) ++
      "mem_data:    %s\n".format(bitsToString(mem_data.values))
    }

    (hlt_e, trp_e, mem_read_e, mem_write_e, mem_addr, mem_data, statusString)
  end buildProcessor

  // Data path with memory interface

  def buildDataPath(read_in: Bus,
                    instr_in: Bus,
                    immed_in: Bus) =

    // Build processor internal state elements

    val host       = read_in.host
    val reg_in     = (0 until num_regs).map(x => host.inputs(wordlength))
    val lcu_e      = host.input()
    val prev_l_idx = host.inputs(num_regs_log2)

    // Build instruction decode unit

    val (l_idx, a_idx, b_idx, i_in, ia_e, ib_e, to_ALU_builder,
         loa_e, sto_e, cmp_e, to_JBU_builder, hlt_e, trp_e) 
           = buildIDU(instr_in, immed_in)

    // Build load completion unit

    val reg_postLCU = buildLCU(reg_in, read_in, prev_l_idx, lcu_e)

    // Build arithmetic logic unit

    val (reg_postALU, f_eq, f_ab, f_gt, opA)
        = buildALU(reg_postLCU, l_idx, a_idx, b_idx, i_in, 
                   ia_e, ib_e, to_ALU_builder, host.False)

    // Build memory interface unit

    val (reg_out, out_l_idx, out_lcu_e, out_lsw_e, 
         mem_read_e, mem_write_e, mem_addr, mem_data) 
           = buildMIU(reg_postALU, l_idx, a_idx, loa_e, sto_e, 
                      host.falses(wordlength), host.False, host.False)

    // Build processor internal feedbacks

    (reg_in zip reg_out).map(x => x._1.buildFeedbackFrom(x._2))
    lcu_e.buildFeedbackFrom(out_lcu_e)
    prev_l_idx.buildFeedbackFrom(out_l_idx)

    (reg_postLCU, mem_read_e, mem_write_e, mem_addr, mem_data)

  end buildDataPath

  // Plain ALU with controls exposed 

  def buildPlainALU(host: Circuit) =

    def opDecoder(labs: Seq[String], ops: Bus) =
      () => {
        val opsf = (labs zip ops.values).filter(_._2)
        if opsf.length == 0 then
          "nop"
        else
          if opsf.length == 1 then
            opsf(0)._1
          else
            "[invalid]"
      }

    def immDecoder(a: Gate, b: Gate) =
      () => {
        if a.value then
          if b.value then
            "[invalid]"
          else
            "[imm operand A]"
        else
          if b.value then
            "[imm operand B]"
          else
            "[not enabled]"
      }

    // ALU internal state elements

    val reg_in  = (0 until num_regs).map(x => host.inputs(wordlength))
    val immed_in = host.inputs(wordlength)
    val ALU_es  = ops_ALU.map(x => host.input())
    val to_ALU_builder = ALU_es zip ops_ALU.map(_._2)
    val l_idx = host.inputs(num_regs_log2)
    val a_idx = host.inputs(num_regs_log2)
    val b_idx = host.inputs(num_regs_log2)
    val imm_a_e = host.input()
    val imm_b_e = host.input()
    val to_trigger = Vector(("L", l_idx.reverse, Trigger.intDecoder(l_idx)),
                            ("A", a_idx.reverse, Trigger.intDecoder(a_idx)),
                            ("B", b_idx.reverse, Trigger.intDecoder(b_idx)),
                            ("operation", new Bus(ALU_es), opDecoder(
                              ops_ALU.map(_._1),new Bus(ALU_es))),
                            ("imm_e",Bus(imm_a_e,imm_b_e),immDecoder(imm_a_e,imm_b_e)))
                  
    // Build arithmetic logic unit

    val (reg_out, f_eq, f_ab, f_gt, opA)
      = buildALU(reg_in, l_idx, a_idx, b_idx, immed_in,
                 imm_a_e, imm_b_e, to_ALU_builder, host.False)

    // Build processor internal feedbacks

    (reg_in zip reg_out).map(x => x._1.buildFeedbackFrom(x._2))

    (reg_in,immed_in,to_trigger)

  end buildPlainALU

  // MEMORY UNIT BUILDER

  // Note: only a simulation -- does not actually run
  //                            a circuit on minilog

  def buildMemory(mem_read_e:  Gate, 
                  mem_write_e: Gate, 
                  mem_addr:    Bus, 
                  mem_data:    Bus, 
                  read_in:     Bus) =

    val host = read_in.host.asInstanceOf[HookedCircuit]
    val mem = new Array[Int](1 << wordlength)
    val memFeedbackHook = () => { // read hook
      val addr = bitsToInt(mem_addr.values)
      val read_e = mem_read_e.value
      val mem_read = intToBits(mem(addr))
      val write_e = mem_write_e.value
      val mem_write = bitsToInt(mem_data.values)
      () => { // read hook returns the write hook
        if read_e then
          (read_in zip mem_read).foreach(x => x._1.set(x._2))
        if write_e then
          mem(addr) = mem_write
      }
    }
    host.buildFeedbackHook(memFeedbackHook)

    // Return memory array

    mem 
  end buildMemory                 

