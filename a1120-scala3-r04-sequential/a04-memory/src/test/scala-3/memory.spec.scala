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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class memorySpec extends AnyFlatSpec with Matchers:
        /** Helper function to set bus values to the given values. */
    def setBusValues(bus: Bus, values: Seq[Boolean]) = {
        require(bus.length == values.length, "Bus length must be equal to the number of given values.")
        (bus zip values).foreach(x => x._1.set(x._2))
    }

    /** Returns a vector with n false values. */
    def falses(n: Int) = (1 to n).map(x => false)
    
    "buildOneWordMemory" should "behaves as specified in the exercise" in {
        /** Create parameters for the one-word memory. 
        The gates have false as the default 
        initial value unless otherwise specified. */
        val dataWord = Seq(true, true, true, false)
        val dataLength = dataWord.length
        val circuit = new Circuit()
        val readEnable = circuit.input()
        val writeEnable = circuit.input()
        val data = circuit.inputs(dataLength)

        /** Build the memory unit. */
        val readOutput = factory.buildOneWordMemory(readEnable, writeEnable, data)

        /** Test readEnable = false, writeEnable = false, 
        memory = empty (falses/zeroes). */
        readEnable.set(false)
        writeEnable.set(false)
        withClue("With readEnable set to false, the output should be zeros. ") {
            readOutput.values should equal (falses(dataLength))
        }

        /** Test readEnable = true, writeEnable = false,
        memory = empty (falses/zeroes). */
        readEnable.set(true)
        withClue("With readEnable set to true but nothing to write, the output should still be zeros. ") {
            readOutput.values should equal (falses(dataLength))
        }

        // /** Clock the circuit and test that nothing is written to the memory. */
        setBusValues(data, dataWord)
        circuit.clock()
        withClue("With data set but writeEnable is false, nothing should be written. ") {
            readOutput.values should equal (falses(dataLength))
        }

        // /** Clock the circuit again with writeEnable = true. Hence, memory = dataWord. */
        writeEnable.set(true)
        circuit.clock()
        withClue("With writeEnable set to true, and data set to (%b, %b, %b, %b), the output should be correct. ".format(dataWord(0), dataWord(1), dataWord(2), dataWord(3))) {
            readOutput.values should equal (dataWord)
        }

        // /** Clock the circuit again with writeEbanble = false. 
        writeEnable.set(false)
        circuit.clock()
        withClue("With writeEnable set to false, and data set to (%b, %b, %b, %b), the output should be correct. ".format(dataWord(0), dataWord(1), dataWord(2), dataWord(3))) {
            readOutput.values should equal (dataWord)
        }

        // /** Test readEnable = false, memory = dataWord. */
        readEnable.set(false)
        withClue("With readEnable set to false, the output should be zeros without the need to clocking. ") {
            readOutput.values should equal (falses(dataLength))
        }
    }

    "buildMemory" should "behaves as specified in the exercise" in {
        /** Create parameters for the one-word memory. 
        The gates have false as the default 
        initial value unless otherwise specified. */
        val word1 = Seq(true, false, false, false)
        val word2 = Seq(false, true, false, false)
        //val word3 = Seq(false, false, true, false)
        //val word4 = Seq(false, false, false, true)
        val addressLength = 2
        val wordLength = 4
        val circuit = new Circuit()
        val readEnable = circuit.input()
        val writeEnable = circuit.input()
        val address = circuit.inputs(addressLength)
        val data = circuit.inputs(wordLength)

        /** Build the memory unit. */
        val readOutput = factory.buildMemory(readEnable, writeEnable, address, data)
        
        /** Test readEnable = false, writeEnable = false, memory = empty (falses/zeroes). */
        readEnable.set(false)
        writeEnable.set(false)
        withClue("With readEnable set to false, the output should be zeros. ") {
            readOutput.values should equal (falses(wordLength))
        }

        /** Test readEnable = true, writeEnable = false, 
        memory = empty (falses/zeroes). */
        readEnable.set(true)
        withClue("With readEnable set to true but nothing to write, the output should still be zeros. ") {
            readOutput.values should equal (falses(wordLength))
        }

        /** Set data to the input bus and clock the circuit and test that 
        nothing is written to the memory (writeEnable = false). */
        setBusValues(address, Seq(false, false))
        setBusValues(data, word1)
        circuit.clock()
        withClue("With data set but writeEnable is false, nothing should be written. ") {
            readOutput.values should equal (falses(wordLength))
        }

        /** Clock the circuit again so that word1 gets written to memory. Hence, 
        memory = (0, 0, 0, word1). */
        writeEnable.set(true)
        circuit.clock()
        withClue("With write enabled, address set to (0, 0), and data is (%b, %b, %b, %b). The output should correspond to word in (0, 0). ".format(word1(0), word1(1), word1(2), word1(3))) {
            readOutput.values should equal (word1)
        }

        /** Read from address (1, 0) which should not contain data. */
        setBusValues(address, Seq(true, false))
        withClue("Address (1, 0) should not contain any data") {
            readOutput.values should equal (circuit.falses(wordLength).values)
        }

        /** Test readEnable = false. */
        setBusValues(address, Seq(false, false))
        readEnable.set(false)
        withClue("With readEnable set to false, the output should be zeros without the need to clocking. ") {
            readOutput.values should equal (falses(wordLength))
        }

        /** Write word2 to memory so that memory = (0, 0, word2, word1) 
        and address = (1, 0). */
        setBusValues(address, Seq(true, false))
        setBusValues(data, word2)
        circuit.clock()
        readEnable.set(true)
        withClue("With read enabled, address set to (1, 0), and data is (%b, %b, %b, %b). The output should correspond to word in (1, 0). ".format(word2(0), word2(1), word2(2), word2(3))) {
            readOutput.values should equal (word2)
        }

        /** Test that word1 is still in memory address (0, 0). */
        setBusValues(address, Seq(false, false))
        withClue("When set the address back to (0, 0), the word should still be in the memory. ") {
            readOutput.values should equal (word1)
        }
    }