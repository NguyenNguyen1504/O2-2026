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





package seqmul:
  import minilog._

  /*
  * Run this object to play with a sequential multiplier in Trigger.
  *
  */
  @main def main(): Unit =

    /** Create new Circuit and Trigger instances. */
    val circuit = new Circuit()
    val trigger = new Trigger(circuit)

    /* Set up inputs */

    // Input size
    val n = 8 
    val input1 = circuit.inputs(n)
    val input2 = circuit.inputs(n)
    val loadEnable = circuit.input()
    trigger.watch("Input 1", input1.reverse)
    trigger.watch("Input 2", input2.reverse)
    trigger.watch("Load enable", loadEnable)

    /* UNCOMMENT lines to run the sequential multiplier. */
    /** Build the sequential multiplier. */
    // val (ready, result) = 
    //   factory.buildSequentialMultiplier(input1, input2, loadEnable)
    
    /** Set up Trigger and go. */
    // trigger.watch("Ready", ready)
    // trigger.watch("Result", result.reverse)
    trigger.go()
  end main

