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


package pipelining

import minilog._

object factory:

  /** Helper functions for building a pipelined multiplier. */

  def buildAdder0(aa: Bus, bb: Bus, c0v: Boolean) =
    new Bus(
      ((aa zip bb)
        .scanLeft(
          (aa.host.False, if c0v then aa.host.True else aa.host.False)
        )) { case ((s, c), (a, b)) =>
          (
            a + b + c,
            (a && b) || (a && c) || (b && c)
          ) // sum-mod-2, majority-of-3
        }
        .drop(1)
        .map(_._1)
    )
  end buildAdder0

  def buildAdder(aa: Bus, bb: Bus) = buildAdder0(aa, bb, false)

  /** This task asks you to implement a builder for a pipelined multiplier for
    * two operands "aa" and "bb" with identical length n, where n is a positive
    * integer power of 2.
    *
    * The multiplier takes as input the operand buses "aa" and "bb", and outputs
    * a bus of length 2*n that supplies the output from the pipeline. Let us
    * refer to the output bus as "result" in what follows.
    *
    * The multiplier must meet the following, more detailed specification. All
    * of the following requirements must be met:
    *
    * 1) At every time step t=0,1,2,... we may feed a pair of operands into the
    * multiplier by setting the input buses "aa" and "bb" equal to the values to
    * be multiplied, and clocking the circuit. Let us refer to these operands as
    * the _operands at time t_.
    *
    * 2) At time step t + log2(n) - 1 the value of "result" must be equal to the
    * product of the operands at time t, for every t=0,1,2,... That is, the
    * pipeline is to consist of log2(n) stages.
    *
    * 3) The circuit depth must not exceed 20*n. [Bonus: you may want to use
    * shallow adders from Round 3 to reduce the depth further for large values
    * of n!]
    *
    * Your function should return the bus "result".
    *
    * Hints: Please refer to the pictures illustrating pipelined circuits in the
    * assignment material and the study material for Round 3. You will need to
    * build internal state elements (that is, input elements) to connect the
    * stages of the pipeline. You can do this by first getting the host circuit
    * (e.g. by calling aa.host) and then requesting one more buses of input
    * elements from the host. A multiplier can be constructed by
    * zeroing-or-shifting one operand 0,1,...,n-1 bits to the left and taking
    * the sum of the n results. This sum can be structured as a perfect binary
    * tree with log2(n) adders along any path from the root to a leaf. One
    * approach to solve this assignment is to structure the stages of the
    * pipeline so that each stage of the pipeline is essentially "one adder
    * deep". That is, each stage carries out the summation associated with one
    * _level_ in the perfect binary tree. A helper function for building adders
    * is given above. Use Trigger to test your design. Yet again the object
    * "play" may be helpful.
    */
  def buildPipelinedMultiplier(aa: Bus, bb: Bus): Bus =
    val host = aa.host
    val length = aa.length
    val targetWidth = length * 2

    var currentBuses: List[Bus] = (0 until length).map( i =>
      val shiftedA = Seq.fill(i)(host.False) ++ aa ++ Seq.fill(targetWidth - length - i)(host.False)
      val result = shiftedA.map(g => g && bb(i))
      new Bus(result)
    ).toList

    while currentBuses.length > 1 do
      val sums = currentBuses.grouped(2).map(part =>
        if part.length == 2 then
          buildAdder(part(0), part(1))
        else
          part(0)
      ).toList

      currentBuses = sums

    currentBuses.head


  end buildPipelinedMultiplier

  
end factory
