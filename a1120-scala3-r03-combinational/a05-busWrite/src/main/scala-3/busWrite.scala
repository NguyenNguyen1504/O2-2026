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


package busWrite

import tinylog._

object factory:

  /** A controllable bus write unit. Specification is as follows:
    *
    * The input to the unit consists of 1) 4 "register" buses, "r0", "r1", "r2",
    * "r3", each consisting of n bits, 2) a bus "data", consisting of n bits, 3)
    * a gate "writeEnable", 4) a bus "writeTo", consisting of 2 bits, and 5) a
    * gate "reset".
    *
    * The output consists of 4 "register" buses "s0", "s1", "s2", "s3", each
    * consisting of n bits.
    *
    * The following three requirements must all hold. a) If "reset" is true,
    * then all bits in each of "s0", "s1", "s2", "s3" must be false. b) If
    * "reset" is false and "write_enable" is false, then the bits of "si" must
    * agree with the bits of "ri", for all i=0,1,2,3. c) If "reset" is false and
    * "write_enable" is true, then the bits of "si" must agree with the bits of
    * "ri", for all i=0,1,2,3 such that i is not equal to j, where j is given by
    * the bits of "write_to" in binary, that is, the least significant bit of j
    * is equal to the value of write_to(0). Furthermore, the bits of "sj" must
    * agree with the bits of "data".
    *
    * That is, in slightly less precise terms, if "reset" is true, then all
    * outputs must be false; otherwise "si" equals "ri" unless "write_enable" is
    * true in which case "sj" equals "data" for j given by "write_to".
    */
  def buildBusWriteUnit4(
      r0: Bus,
      r1: Bus,
      r2: Bus,
      r3: Bus,
      data: Bus,
      writeEnable: Gate,
      writeTo: Bus,
      reset: Gate
  ): (Bus, Bus, Bus, Bus) =
    require(r0.length > 0, "The register buses cannot be empty")
    require(r0.length == r1.length, "The register buses must be of same width")
    require(r0.length == r2.length, "The register buses must be of same width")
    require(r0.length == r3.length, "The register buses must be of same width")
    require(
      r0.length == data.length,
      "The data bus must be of same width as the register buses"
    )
    require(writeTo.length == 2)
    val w0 = !writeTo(1) && !writeTo(0)
    val w1 = !writeTo(1) &&  writeTo(0)
    val w2 =  writeTo(1) && !writeTo(0)
    val w3 =  writeTo(1) &&  writeTo(0)
    
    val selected0 = w0 && writeEnable
    val selected1 = w1 && writeEnable
    val selected2 = w2 && writeEnable
    val selected3 = w3 && writeEnable
    
    def process(r: Bus, selected: Gate): Bus =
      val write = data.map(_ && selected) | r.map(_ && !selected)
      write.map(_ && !reset)
      
    val s0 = process(r0, selected0)
    val s1 = process(r1, selected1)
    val s2 = process(r2, selected2)
    val s3 = process(r3, selected3)

    (s0, s1, s2, s3)
    
  end buildBusWriteUnit4

end factory
