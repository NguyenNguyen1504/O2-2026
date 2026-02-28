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


package rationalDecompose

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class rationalDecomposeSpec extends AnyFlatSpec with Matchers:
    "decompose" should "return the period part with the correct length" in {
        val test = Seq(((557, 12345, 10), 822),
        ((34567, 98765, 11), 19752),
        ((34567, 98765,  2), 3292),
        ((  567,   991,  2), 495),
        ((    1, 15017, 10), 15016))

        for ((p, q, b), answer) <- test do
            withClue("For p = %d, q = %d, b = %d, ".format(p, q, b)) {
                decompose(p, q, b)._3.length shouldBe answer
            }
    }

    "decompose" should "produce the correct results" in {
        val test = Seq((( 5, 1, 10), (List(5), List[Int](), List(0))),
        ((98, 10, 10), (List(9), List(8), List(0))),
        ((123, 1, 10), (List(1,2,3), List[Int](), List(0))),
        ((45, 99, 10), (List(0), List[Int](), List(4,5))),
        ((34038, 275, 10), (List(1,2,3), List(7,7), List(4,5))),
        ((18245, 19998, 10), (List(0), List(9), List(1,2,3,4))))

        for ((p, q, b), answer) <- test do
            withClue("For p = %d, q = %d, b = %d, ".format(p, q, b)) {
                decompose(p, q, b) should equal (answer)
            }
    }
