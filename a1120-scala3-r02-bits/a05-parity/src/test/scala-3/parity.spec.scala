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


package parity

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class paritySpec extends AnyFlatSpec with Matchers:

    // Helper function to create binary strings with the leading zeros
    private def fullBinStr(i: Long): String =
        val base = i.toBinaryString
        return "0" * (64 - base.length) + base
    end fullBinStr

    // The test input and the expected answers. The format is Seq((input, answer)).
    "extend" should "return the correct augmented payload" in {
        val test = Seq((0x0000000000000000L, 0x0000000000000000L), 
        (0x0000000000000001L, 0x8000000000000001L), 
        (0x0000000000000011L, 0x0000000000000011L), 
        (0x8000000000000000L, 0x0000000000000000L), 
        (0x8000000000002000L, 0x8000000000002000L), 
        (0x8000000000F00000L, 0x0000000000F00000L), 
        (0x4000000000000000L, 0xC000000000000000L), 
        (0x0000001000300000L, 0x8000001000300000L))

        for (input, answer) <- test do
            val result = extend(input)
            withClue("BINARY\nFor input %s,\nresult    %s\nwas not   %s\nDECIMAL\n"
                .format(fullBinStr(input), fullBinStr(result), fullBinStr(answer))) {
                    result shouldBe answer
            }
    }

    // The test input. The format is Seq(input).
    "ok" should "return True when the parity bit matches the parity of the payload" in {
        val test = Seq(0x0000000000000000L,
                       0x8000000000000001L,
                       0x0000000000000011L,
                       0x0000000000F00000L,
                       0x8000001000300000L)

        for input <- test do
            withClue("For input %s,\nthe obtained result".format(fullBinStr(input))) {
                ok(input) shouldBe true
            }
    }

    // The test input.
    "ok" should "return False when the parity bit does not match the parity of the payload" in {
        val test = Seq(0x0000000100000000L, 
                       0x8000000001000001L,
                       0x0000010000000011L,
                       0x8000000000000000L,
                       0x0000000000E00000L,
                       0x8000005000300000L)

        for input <- test do
            withClue("For input %s,\nthe obtained result".format(fullBinStr(input))) {
                ok(input) shouldBe false
            }
    }