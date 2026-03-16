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

  /*
  * Run this to play with the bus write unit in Toggler.
  *
  */
  @main def main(): Unit =
    val toggler = new Toggler()
    val n = 8
    val r0 = Bus.inputs(n)
    val r1 = Bus.inputs(n)
    val r2 = Bus.inputs(n)
    val r3 = Bus.inputs(n)
    val data = Bus.inputs(n)
    val writeEnable = Gate.input()
    val writeTo = Bus.inputs(2)
    val reset = Gate.input()
    toggler.clear()
    toggler.watch("r0", r0.reverse)
    toggler.watch("r1", r1.reverse)
    toggler.watch("r2", r2.reverse)
    toggler.watch("r3", r3.reverse)
    toggler.watch("data", data.reverse)
    toggler.watch("writeEnable", writeEnable)
    toggler.watch("writeTo", writeTo.reverse)
    toggler.watch("reset", reset)
    // UNCOMMENT Below when you have implemented the task 
    // and want to play with your circuit
    /*
    val (s0, s1, s2, s3) = factory.buildBusWriteUnit4(r0, r1, r2, r3, data, writeEnable, writeTo, reset)
    toggler.watch("s0", s0.reverse)
    toggler.watch("s1", s1.reverse)
    toggler.watch("s2", s2.reverse)
    toggler.watch("s3", s3.reverse)
    */
    toggler.go()
  end main

