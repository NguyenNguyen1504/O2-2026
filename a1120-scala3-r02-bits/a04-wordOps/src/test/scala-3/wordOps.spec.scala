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


package wordOps

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class wordOpsSpec extends AnyFlatSpec with Matchers:
  // popCount test
  "popCount" should "returns the correct number of 1-bits in the given words" in {
    // The test input and the expected answers. The format is Seq(((input, format), answer)). The format is used to print the clue
    val test = Seq(
      ((0x00000000, "0x00000000"), 0),
      ((0x11111111, "0x11111111"), 8),
      ((0xaaaaaaaa, "0xAAAAAAAA"), 16),
      ((0xffffffff, "0xFFFFFFFF"), 32)
    )

    for ((input, format), answer) <- test do
      withClue("For input %s, ".format(format)) {
        popCount(input) shouldBe answer
      }
  }

  // reverse test
  "reverse" should "correctly reverses the bit order of the given words" in {
    // The test input and the expected answers. The format is Seq(((input, format), answer)). The format is used to print the clue
    val test = Seq(
      ((0x0000, "0x0000"), 0x0000),
      ((0xffff, "0xFFFF"), 0xffff),
      ((0xaaaa, "0xAAAA"), 0x5555),
      ((0x1234, "0x1234"), 0x2c48)
    )

    for ((input, format), answer) <- test do
      withClue("For input %s, ".format(format)) {
        reverse(input.toShort) shouldBe (answer.toShort)
      }
  }

  // leftRotate test
  "leftRotate" should "returns the correct results for the given inputs" in {
    // The test input and the expected answers. The format is Seq((input1, input2, format), answer)). The format is used to print the clue
    val test = Seq(
      ((0x0000000000000000L, 0, "0x0000000000000000L"), 0x0000000000000000L),
      ((0x8000000000000002L, 1, "0x8000000000000002L"), 0x0000000000000005L),
      ((0x8000000000000002L, -2, "0x8000000000000002L"), 0xa000000000000000L),
      ((0x1234567890abcdefL, 33, "0x1234567890ABCDEFL"), 0x21579bde2468acf1L)
    )

    for ((input1, input2, format), answer) <- test do
      withClue("For input %s and %d, ".format(format, input2)) {
        leftRotate(input1, input2) shouldBe (answer)
      }
  }

  it should "work for large values of k" in {
    val test = Seq(
      (
        (0x0000000000000000L, 1290866239, "0x0000000000000000L"),
        0x0000000000000000L
      ),
      (
        (0x8000000000000002L, -1427291795, "0x8000000000000002L"),
        87960930222080L
      ),
      (
        (0x8000000000000002L, -1512019868, "0x8000000000000002L"),
        171798691840L
      ),
      (
        (0x1234567890abcdefL, 1919183135, "0x1234567890ABCDEFL"),
        5212326094582721340L
      )
    )

    for ((input1, input2, format), answer) <- test do
      withClue("For input %s and %d, ".format(format, input2)) {
        leftRotate(input1, input2) shouldBe (answer)
      }
  }
