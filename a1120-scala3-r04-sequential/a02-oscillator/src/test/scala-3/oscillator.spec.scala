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


package oscillator

import minilog._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class oscillatorSpec extends AnyFlatSpec with Matchers:
    "buildOscillatorPeriod2" should "give the correct bit at any given time" in {
        val clocks = 100
    
        /** Build the oscillator circuit. */
        val circuit = new Circuit()
        val state: Bus = factory.buildOscillatorPeriod2(circuit)
        
        /** Clock the oscillator and check the output (taken from state(0)) 
            after each clocking. The output should be true for 
            t = 0, 2, 4, ... and false otherwise. */

        for clock <- 0 until clocks do
            val output = state(0)
            if clock%2 == 0 then 
                withClue("At time %d, the output should be true. ".format(clock)) {
                    output.value shouldBe true
                }
            else 
                withClue("At time %d, the output should be false. ".format(clock)) {
                    output.value shouldBe false
                }
            circuit.clock()
    }

    "buildOscillatorPeriod3" should "give the correct bit at any given time" in {
        /** The number of times the oscillator is clocked during the test. */
        val clocks = 100

        /** Build the oscillator circuit. */
        val circuit = new Circuit()
        val state: Bus = factory.buildOscillatorPeriod3(circuit)

        /** Clock the oscillator and check the output (taken from state(0)) 
        after each clocking. The output should be true for t = 0, 3, 6, ... 
        and false otherwise. */

        for clock <- 0 until clocks do 
            val output = state(0)
            if clock%3 == 0 then
                withClue("At time %d, the output should be true. ".format(clock)) {
                    output.value shouldBe true
                }
            else
                withClue("At time %d, the output should be false. ".format(clock)) {
                    output.value shouldBe false
                }
            circuit.clock()
    }

    "buildOscillatorPeriod4" should "give the correct bit at any given time" in {     
        /** The number of times the oscillator is clocked during the test. */
        val clocks = 100

        /** Build the oscillator circuit. Note that buildOscillatorPeriod4 returns 
            the output gate instead of the full oscillator state as in the previous
            test cases. */
        val circuit = new Circuit()
        val output: Gate = factory.buildOscillatorPeriod4(circuit)
    
        /** Clock the oscillator and check the output after each clocking. 
            The output should be true for t = 0, 4, 8, ... and false otherwise. */

        for clock <- 0 until clocks do
            if clock%4 == 0 then
                withClue("At time %d, the output should be true. ".format(clock)) {
                    output.value shouldBe true
                }         
            else
                withClue("At time %d, the output should be false. ".format(clock)) {
                    output.value shouldBe false
                }    
            circuit.clock()
    }

    "buildOscillator" should "give the correct bit at any given time" in {    
        /** The number of times the oscillator is clocked during the test. */
        val clocks = 100
        
        /** The upper bound for oscillator periods used in the tests. */
        val maxPeriod = 5
        
        /** Test the oscillator with periods from 1 to maxPeriod. */
        for period <- 1 to maxPeriod do {
            /** Build the oscillator circuit. */
            val circuit = new Circuit()
            val output = factory.buildOscillator(circuit, period)

        /** Clock the oscillator and check the output after each clocking. 
          The output should be true for t = 0, period, 2*period, ... and 
          false otherwise. */

            for clock <- 0 until clocks do 
                if clock%period == 0 then
                    withClue("With period %d, at time %d the output should be true. ".format(period, clock)) {
                        output.value shouldBe true
                    }
                else 
                    withClue("With period %d, at time %d the output should be false. ".format(period, clock)) {
                        output.value shouldBe false
                    }
                circuit.clock()
        }
    }
