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





package circuits:

  import tinylog._

 /*
 * Run this object to play with your designs in Toggler.
 * If you want to play with other methods than the adder, 
 * just modify the code below.
 *
 */ 
  @main def main(): Unit =
    val toggler = new Toggler()
    val n = 4
    val aa = Bus.inputs(n)
    val bb = Bus.inputs(n)
    val cc = factory.buildAdder(aa, bb)
    toggler.watch("aa", aa.reverse)
    toggler.watch("bb", bb.reverse)
    toggler.watch("cc", cc.reverse)
    toggler.go()
  end main

