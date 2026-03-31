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





package oscillator:
  import minilog._

/*
 * Run this object to play with oscillators in Trigger.
 */
  @main def main(): Unit =

    /** Create new Circuit and Trigger instances. */
    val circuit = new Circuit()
    val trigger = new Trigger(circuit)
    
    /** Oscillator with period 2 (full circuit state is shown). */
    val stateP2: Bus = factory.buildOscillatorPeriod2(circuit)
    trigger.watch("State of an oscillator with period 2", stateP2.reverse)
    
    /** Oscillator with period 3 (full circuit state is shown). */
    val stateP3: Bus = factory.buildOscillatorPeriod3(circuit)
    trigger.watch("State of an oscillator with period 3", stateP3.reverse)
    
    /** Oscillator with period 4 (only the output is shown). */
    // val outputP4: Gate = factory.buildOscillatorPeriod4(circuit)
    // trigger.watch("Output of an oscillator with period 4", outputP4)

    /** Oscillator with a user-defined period (only the output is shown). */
    // val period = 3
    // val output: Gate = factory.buildOscillator(circuit, period)
    // trigger.watch("Output of an oscillator with period %d".format(period), output)
 	
    /** Go Trigger. */
    trigger.go()
  end main

