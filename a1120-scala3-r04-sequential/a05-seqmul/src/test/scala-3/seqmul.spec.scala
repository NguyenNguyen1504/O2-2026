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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class seqmulSpec extends AnyFlatSpec with Matchers:
    /** Helper function to set bus values to the given values. */
    def setBusValues(bus: Bus, values: Seq[Boolean]) = {
        require(bus.length == values.length, 
            "Bus length must be equal to the number of given values.")
        (bus zip values).foreach(x => x._1.set(x._2))
    }

    /** Returns the Boolean vector representation of an integer. 
         The returned array has exactly the number of boolean values indicated 
        by 'size'. Too large input values are truncated. */
    def booleanOf(value: Int, size: Int) = {
        require(value >= 0, "Given value must be non-negative.")
        require(size > 0, "Given size must be positive.")
        (0 until size).map(j => ((value >> j)&1) == 1)
    }
    
    /** Returns a vector with n false values. */
    def falses(n: Int) = (1 to n).map(x => false)

    "buildSequentialMultiplier" should "behave as specified in the exercise" in {
        /** Build the sequential multiplier using input bus widths defined by n 
        and m. The width of the result bus should be equal to the sum 
        of the widths of input1 and input2, i.e., the sum of n and m. */
        val n = 8
        val m = 6
        val circuit = new Circuit()
        val input1 = circuit.inputs(n)
        val input2 = circuit.inputs(m)
        val loadEnable = circuit.input()
        val (ready, result) = 
        factory.buildSequentialMultiplier(input1, input2, loadEnable)
        
        /** Perform the test using the input pairs given in the following list. */
        val inputPairs = List((0, 0), (1, 0), (123, 7), (201, 49))
        for (a, b) <- inputPairs do {   
            /** Load the values to the multiplier and check that the value of ready 
                 is not true before loadEnable is false. */
            setBusValues(input1, booleanOf(a, n))
            setBusValues(input2, booleanOf(b, m))
            loadEnable.set(true)
            withClue("ready should be false when the multiplication is not completed. Inputs: %d, %d. ".format(a, b)) {
                ready.value shouldBe false
            }
            circuit.clock()
            withClue("ready should be false when the multiplication is not completed. Inputs: %d, %d. ".format(a, b)) {
                ready.value shouldBe false
            }
            loadEnable.set(false)

            /** Clock the multiplier 2*(n+m)+1 times which should be enough to get 
                 the result. */
            for i <- 1 to 2*(n+m)+1 do 
                circuit.clock()
                // If ready bit is set, result must contain the correct outcome
                if ready.value then
                    withClue("After %d clock(s) ready is true but result is incorrect.".format(i) +
                    " Inputs: %d, %d.\n".format(a, b)) {
                        result.values should equal (booleanOf(a*b, n+m))
                    }

            /** Check other things after multiplication is completed */
            withClue("Too many gates for inputs: %d, %d. ".format(a, b)) {
                circuit.numberOfGates() should be <= (30*(n+m))
            }
            withClue("ready should be true when the multiplication is completed. ") {
                ready.value shouldBe true
            }
            withClue("loadEnable should be false. ") {
                loadEnable.value shouldBe false
            }
            withClue("Incorrect result. Inputs: %d, %d. ".format(a, b)) {
                result.values should equal (booleanOf(a*b, n+m))
            }
        }
    }
