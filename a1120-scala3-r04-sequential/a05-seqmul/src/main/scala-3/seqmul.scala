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


package seqmul

import minilog._

object factory:

  /** Helper functions for building a sequential multiplier. */

  def buildAdder0(aa: Bus, bb: Bus, c0v: Boolean) =
    new Bus(
      (aa zip bb)
        .scanLeft(
          (aa.host.False, if c0v then aa.host.True else aa.host.False)
        ) { case ((s, c), (a, b)) =>
          (
            a + b + c,
            (a && b) || (a && c) || (b && c)
          ) // sum-mod-2, majority-of-3
        }
        .drop(1)
        .map(_._1)
    )
  end buildAdder0

  // Builds an adder using buildAdder0
  def buildAdder(aa: Bus, bb: Bus) = buildAdder0(aa, bb, false)

  /** This task asks you to implement a builder for a sequential logic unit that
    * multiplies two integers.
    *
    * The multiplier takes the following inputs. First, there are two operand
    * buses, "aa" and "bb", whose product is to be computed and given as output.
    * Second, there is an enable bit, "loadEnable", for loading operands and
    * starting multiplication. There are two outputs, a gate "ready" and a bus
    * "result". The bus "result" must have length at least the length of "aa"
    * plus the length of "bb".
    *
    * The multiplier must meet the following, more detailed specification. All
    * of the following requirements must be met:
    *
    * 1) A multiplication is initialized by setting "aa" and "bb" to the values
    * to be multiplied, setting "loadEnable" to true, and clocking the circuit.
    * This combination is referred to as a "load" in what follows.
    *
    * 2) When "loadEnable" is true, "ready" must be false.
    *
    * 3) After a "load" clocks the circuit, and "loadEnable" is set to false
    * during subsequent clocks, "ready" may be false for at most
    * 2*(aa.length+bb.length)+1 subsequent clocks. (That is, the multiplication
    * must complete in at most this number of clocks unless there is a new
    * "load".)
    *
    * 4) When "ready" is true, "result" contains the product of the operands
    * that were loaded during the most recent "load".
    *
    * 5) Once "ready" is true, it must remain true unless there is a new "load".
    *
    * 6) The constructed circuit may have at most 30*(aa.length+bb.length)
    * gates, including logic gates and input elements. In particular, a
    * combinational multiplier will not do.
    *
    * Your function should return the gate "ready" and the bus "result", in this
    * order.
    *
    * Hints: You will need to build internal state elements (that is, input
    * elements) to store the operands and the result inside the unit. You can do
    * this by first getting the host circuit (e.g. by calling loadEnable.host)
    * and then requesting one more buses of input elements from the host. A
    * simple solution is to have equal-length buses for storing both operands
    * and the result. Shift one operand to the left and the other operand to the
    * right when the clock triggers. Accumulate the result with a single adder.
    * The multiplication is ready when one of the operands becomes zero.
    * (Bitwise NOT the bus and reduce with AND to test this.) A helper function
    * for building adders is given above. Use Trigger to test your design. Yet
    * again the object "play" may be helpful.
    */
  def buildSequentialMultiplier(
      aa: Bus,
      bb: Bus,
      loadEnable: Gate
  ): (Gate, Bus) =
    val host   = loadEnable.host
    val regA   = new Bus(host.inputs(aa.length + bb.length))
    val regB   = new Bus(host.inputs(bb.length))
    val regRes = new Bus(host.inputs(aa.length + bb.length))

    val shiftedA = host.False +: regA.dropRight(1)
    val nextA = ((aa ++ Seq.fill(bb.length)(host.False)) zip shiftedA).map( (input, shifted) =>
      (input && loadEnable) || (shifted && !loadEnable)
    )
    regA.buildFeedbackFrom(new Bus(nextA))

    val shiftedB = regB.tail :+ host.False
    val LSBregB = regB.head
    val nextB = (bb zip shiftedB).map( (input, shifted) =>
      (input && loadEnable) || (shifted && !loadEnable)
    )
    regB.buildFeedbackFrom(new Bus(nextB))

    val sum = buildAdder(regRes, regA)
    val nextRes = (regRes zip sum).map( (oldBits, sumBits) =>
      val addBits = (sumBits && LSBregB) || (oldBits && !LSBregB)
      (host.False && loadEnable) || (addBits && !loadEnable)
    )
    regRes.buildFeedbackFrom(new Bus(nextRes))

    val bZero = regB.map(!_).reduce(_&&_)
    val regReady = new Bus(host.inputs(1))
    val nextReady = (!loadEnable && bZero)
    regReady.buildFeedbackFrom(new Bus(Seq(nextReady)))
    val ready = regReady.head && !loadEnable

    (ready, regRes)
  end buildSequentialMultiplier

end factory
