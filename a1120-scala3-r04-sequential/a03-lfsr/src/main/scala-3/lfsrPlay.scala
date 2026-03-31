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





package lfsr:

  import minilog._

  /*
  * Run this object to play with LFSRs in Trigger.
  *
  * NOTE: YOU WILL NEED TO UNCOMMENT THE CORRECT CASES IN THE CODE BELOW
  *       FOR THIS PRORGRAM TO DO ANYTHING!
  */

  @main def main(): Unit =

    /** Create new Circuit and Trigger instances. */
    val circuit = new Circuit()
    val trigger = new Trigger(circuit)
    
    /** Rotator unit. */
    /* Use this block to visualize the rotator.*/
    // val state1 = circuit.inputs(4)
    // val rotator = factory.buildRotator(state1)
    // state1(0).set(true)
    // trigger.watch("Rotator", rotator.reverse)
    
    /** A maximal 5-bit LFSR. */
    /* Use this block to visualize the 5-bit LFSR. */
    // val state2 = circuit.inputs(5)
    // val fiveBitLFSR = factory.build5BitLFSR(state2)
    // state2(0).set(true)
    // trigger.watch("5-bit LFSR", fiveBitLFSR.reverse)
    
    /** LFSR unit. */
    /* Use this block to visualize a general LFSR unit.*/
    // val state3 = circuit.inputs(4)
    // val taps = List(1)
    // val LFSR = factory.buildLFSR(taps, state3)
    // state3(0).set(true)
    // trigger.watch("LFSR", LFSR.reverse)
 	
    /** Go Trigger. */
    // DO NOT FORGET TO UNCOMMENT trigger.go() to start the UI!
    // trigger.go()
  end main
