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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class pipeliningSpec extends AnyFlatSpec with Matchers:
      
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

    /** Base-2 logarithm. */
    def log2(x: Double) = scala.math.log(x)/scala.math.log(2)

    /** Rounds the given double up to the nearest integer. */
    def roundUp(x: Double): Int = scala.math.ceil(x).toInt

    "buildPipelinedMultiplier" should "behave as specified in the exercise" in {    
    /** Build the pipelined multiplier using the bus width defined by n. */
        val n = 16
        val circuit = new Circuit()
        val input1 = circuit.inputs(n)
        val input2 = circuit.inputs(n)
        val output = factory.buildPipelinedMultiplier(input1, input2)
        
        /** Bounds for execution time and circuit depth. */
        val timeBound = roundUp(log2(n)) - 1
        val depthBound = 20*n
        val inputPairs = List((0, 0), (123, 0), (1<<8 - 1, 123), (1<<8 - 1, 1<<11))
        for ((a, b) <- inputPairs) {
            /** Load the values for the multiplier. */
            setBusValues(input1, booleanOf(a, n))
            setBusValues(input2, booleanOf(b, n))
            
            /** Clock the multiplier timeBound times which should be enough to get 
             the result. */
            for (i <- 1 to timeBound) circuit.clock()

            /** Check the result. */
            val depth = circuit.depth
            withClue("Too deep circuit. Inputs: %d, %d.".format(a, b)) {
                depth should be <= (depthBound)
            }
            withClue("Incorrect result. Inputs: %d, %d.".format(a, b)) {
                output.values should equal (booleanOf(a*b, 2*n))
            }
        }
    }
