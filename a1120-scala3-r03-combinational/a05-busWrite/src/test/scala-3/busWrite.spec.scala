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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class busWriteTest extends AnyFlatSpec with Matchers:
    
    // Helper function to assign values to a bus
    def setBus(b: Bus, values: Boolean*) : Unit = {
        require(b.forall(g => g.isInstanceOf[InputElement]), "The bus must consist of input elements only")
        require(values.length == b.length, "The number of values must match with bus width")
        (b zip values).foreach({ case (g: InputElement, v: Boolean) => g.set(v); case _ => require(false) })
    }

    "All buses" should "contain false bits when reset is set to true" in {
        val n = 4
        val r0 = Bus.inputs(n)
        val r1 = Bus.inputs(n)
        val r2 = Bus.inputs(n)
        val r3 = Bus.inputs(n)
        val data = Bus.inputs(n)
        val writeEnable = Gate.input()
        val writeTo = Bus.inputs(2)
        val reset = Gate.input()
        // Build the unit
        val (s0, s1, s2, s3) = factory.buildBusWriteUnit4(r0, r1, r2, r3, data, writeEnable, writeTo, reset)
        // OK the unit is now built, so let us feed in some input
        setBus(r0, true, false, false, false)
        setBus(r1, true, false, true, false)
        setBus(r2, true, false, false, true)
        setBus(r3, false, false, false, false)
        setBus(data, true, true, true, false)
        writeEnable.set(true)
        setBus(writeTo, true, false)
        reset.set(true)

        for s <- Seq(s0, s1, s2, s3) do
            withClue("When reset is true") {
                s.values should equal (Seq(false, false, false, false))
            }
    }

    "The bits of si" should "agree with the bits of ri when write_enable is set to false" in {
        val n = 4
        val r0 = Bus.inputs(n)
        val r1 = Bus.inputs(n)
        val r2 = Bus.inputs(n)
        val r3 = Bus.inputs(n)
        val data = Bus.inputs(n)
        val writeEnable = Gate.input()
        val writeTo = Bus.inputs(2)
        val reset = Gate.input()
        // Build the unit
        val (s0, s1, s2, s3) = factory.buildBusWriteUnit4(r0, r1, r2, r3, data, writeEnable, writeTo, reset)
        // OK the unit is now built, so let us feed in some input
        setBus(r0, true, false, false, false)
        setBus(r1, true, false, true, false)
        setBus(r2, true, false, false, true)
        setBus(r3, false, false, false, false)
        setBus(data, true, true, true, false)
        writeEnable.set(false)
        setBus(writeTo, true, false)
        reset.set(false)

        val sr = Seq(s0, s1, s2, s3).zip(Seq(r0, r1, r2, r3))    
        for (s, r) <- sr do
            withClue("With r%d = Bus(%b, %b, %b, %b)".format(sr.indexOf((s, r)), r(0).value, r(1).value, r(2).value, r(3).value)) {
                s.values should equal (r.values)
            }
    }

    "The data" should "be written correctly when reset is set to false and write_enable is set to true" in {
        val n = 4
        val r0 = Bus.inputs(n)
        val r1 = Bus.inputs(n)
        val r2 = Bus.inputs(n)
        val r3 = Bus.inputs(n)
        val data = Bus.inputs(n)
        val writeEnable = Gate.input()
        val writeTo = Bus.inputs(2)
        val reset = Gate.input()
        // Build the unit
        val (s0, s1, s2, s3) = factory.buildBusWriteUnit4(r0, r1, r2, r3, data, writeEnable, writeTo, reset)
        // OK the unit is now built, so let us feed in some input
        setBus(r0, true, false, false, false)
        setBus(r1, true, false, true, false)
        setBus(r2, true, false, false, true)
        setBus(r3, false, false, false, false)
        setBus(data, true, true, true, false)
        writeEnable.set(true)
        reset.set(false)
        
        val addresses = Seq((0, false, false), (1, true, false), (2, false, true), (3, true, true))

        for (address, bit0, bit1) <- addresses do
            val expected = Array(r0, r1, r2, r3)
            expected(address) = data
            setBus(writeTo, bit0, bit1)
            for (s, answer) <- Seq(s0, s1, s2, s3).zip(expected) do
                withClue("With writeTo = Bus(%b, %b) and data = Bus(%b, %b, %b, %b)"
                    .format(writeTo(0).value, writeTo(1).value, data(0).value, data(1).value, data(2).value, data(3).value)) {
                        s.values should equal (answer.values)
                }
    }
