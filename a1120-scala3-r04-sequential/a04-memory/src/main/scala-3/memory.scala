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


package memory

import minilog._

object factory:

  /* Helper functions for building memory units. */

  /** Takes as input a b-bit bus and outputs a 2^b-bit bus with exactly one bit
    * set to true, namely the bit whose index is given in binary by the b-bit
    * bus.
    */
  def buildDecoder(aa: Bus): Bus =
    aa.foldLeft(Bus(aa.host.True))((d, g) => (d && !g) ++ (d && g))
  end buildDecoder

  /** Takes as input two buses, in0 and in1, and a selector gate. The output is
    * in0 if the selector gate is false and in1 if the selector gate is true.
    */
  def buildTwoBusSelector(in0: Bus, in1: Bus, sel: Gate): Bus =
    (in0 && !sel) | (in1 && sel)
  end buildTwoBusSelector

  /** Takes as input a sequence of m b-bit buses and an m-bit selector bus, the
    * latter with exactly one bit having the value true, and outputs the
    * selected b-bit bus.
    */
  def buildBusSelector(in: Seq[Bus], sel: Bus): Bus =
    (in zip sel).map(x => x._1 && x._2).reduceLeft(_ | _)
  end buildBusSelector

  /** This task asks you to implement a builder for a sequential logic unit that
    * implements one word of memory.
    *
    * The one-word memory unit must support two basic operations: reading and
    * writing a word. These operations amount to supporting the following, more
    * detailed specification. All of the following requirements must be met:
    *
    * 1) A read is issued at time t by setting "readEnable" to true. In this
    * case the output bus at time t must have values identical to the word
    * stored in the unit. Reading the memory should not require the circuit to
    * be clocked.
    *
    * 2) If "readEnable" is false at time t, each gate in the output bus at time
    * t must also have value false. As in the previous case, clocking the
    * circuit should not be required for the effect to take place.
    *
    * 3) A write is issued at time t by setting "writeEnable" to true, setting
    * "data" to equal the word that we want to store in the unit, and clocking
    * the circuit. When the clock triggers, "data" should be saved to the word
    * stored inside the unit. (Thus, in particular the output of a read at time
    * t+1 should be identical to "data" at time t, assuming "writeEnable" was
    * true at time t and the clock was triggered.)
    *
    * 4) If "writeEnable" is false at time t, clocking the circuit should not
    * affect the word stored in the unit. In particular, the contents of "data"
    * at time t should not affect the word stored in the unit.
    *
    * Your function should return an output bus matching this specification.
    *
    * Hints: You will need to build internal state elements (that is, input
    * elements) to store data in the unit. You can do this by first getting the
    * host circuit (e.g. by calling readEnable.host) and then requesting a bus
    * of input elements (with length identical to "data") from the host. Think
    * carefully how to build feedbacks to the internal state elements. A direct
    * feedback from "data" will not work, but rather you want a feedback either
    * from the internal state or "data". This requires combinational logic. For
    * example, the function buildBusSelector given above may be helpful. Use
    * Trigger to test your design. Again the object "play" may be helpful.
    */
  def buildOneWordMemory(readEnable: Gate, writeEnable: Gate, data: Bus): Bus =
    require(data.length > 0, "Data bus must have positive width.")
    val length = data.length
    val falseBus = data.host.falses(length)
    val state = data.host.inputs(length)

    val stateOutput = buildTwoBusSelector(state, data, writeEnable)
    for i <- 0 until length do
      state(i).buildFeedbackFrom(stateOutput(i))
    val out = buildTwoBusSelector(falseBus, state, readEnable)
    out
  end buildOneWordMemory

  /** In this task we get more ambitious. Namely, we want a memory unit capable
    * of storing multiple words. In fact, 2^b words to be precise, with b >= 1.
    *
    * The unit must support reads and writes addressing to specific words among
    * the 2^b words. The address is supplied in the b-bit bus "address". That
    * is, b is equal to address.length. In precise terms, all of the following
    * requirements must be met:
    *
    * 1) A read is issued at time t by setting "readEnable" to true and setting
    * "address" to the address of the word that we want to read. In this case
    * the output bus at time t must have values identical to the word with
    * address "address" stored in the unit. Reading the memory should not
    * require the circuit to be clocked.
    *
    * 2) If "readEnable" is false at time t, each gate in the output bus at time
    * t must also have value false. Clocking the circuit should not be required
    * for the effect to take place.
    *
    * 3) A write is issued at time t by setting "writeEnable" to true, setting
    * "data" to equal the word that we want to store in the unit, setting
    * "address" to equal the address of the word that is to receive the write,
    * and clocking the circuit. When the clock triggers, "data" should be saved
    * to the word with address "address" stored inside the unit.
    *
    * 4) If "writeEnable" is false at time t, clocking the circuit should not
    * affect any of the words stored in the unit. In particular, the contents of
    * "data" at time t should not affect any of the words stored in the unit.
    *
    * Your function should return an output bus matching this specification.
    *
    * Hints: If you want, you may use buildOneWordMemory as a subroutine to
    * build the memory unit. You may want to use decoder circuits built with
    * buildDecoder to select where to write and where to read. Bus selectors can
    * be built with buildBusSelector. Use Trigger to test your design.
    */
  def buildMemory(
      readEnable: Gate,
      writeEnable: Gate,
      address: Bus,
      data: Bus
  ): Bus =
    require(address.length > 0, "Address bus must have positive width.")
    require(data.length > 0, "Data bus must have positive width.")
    val decodedAddress = buildDecoder(address)
    val length = data.length * address.length
    val falseBus = data.host.falses(length)

    val memories = (0 until decodedAddress.length).map( i =>
      val cellWriteEnable = writeEnable && decodedAddress(i)
      buildOneWordMemory(data.host.True, cellWriteEnable, data)
    )

    val selectedWord = buildBusSelector(memories, decodedAddress)

    val out = buildTwoBusSelector(falseBus, selectedWord, readEnable)

    out
  end buildMemory
