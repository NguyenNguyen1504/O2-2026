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


package gatetasks

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

import tinylog._

class gateTest extends AnyFlatSpec with Matchers:
    "buildXOR" should "return the correct XOR gate" in {
        val a = new InputElement
        val b = new InputElement
        val x = factory.buildXOR(a, b)

        for av <- Seq(true, false); bv <- Seq(true, false) do
            a.set(av)
            b.set(bv)
            val answer = (av != bv)
            withClue("With a = %b, b = %b, ".format(av, bv)) {
                x.value shouldBe(answer)
            }
    }

    "buildMajorityOfThree" should "return the correct gate" in {
        val a = new InputElement()
        val b = new InputElement()
        val c = new InputElement()
        val g1 = factory.buildMajorityOfThree(a, b, c)
        val v = Seq(true, false)
        
        for av <- v; bv <- v; cv <- v do
            a.set(av)
            b.set(bv)
            c.set(cv)
            val answer = Seq(av, bv, cv).count(b => b) >= 2
            withClue("With a = %b, b = %b, c = %b, ".format(av, bv, cv)) {
                g1.value shouldBe(answer)
            }
    }
