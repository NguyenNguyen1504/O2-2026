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


package collatz

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class CollatzSpec extends AnyFlatSpec with Matchers:

  "next" should "work correctly for a small set of even and odd numbers" in {
    // Known f(n) for some inputs n: n->f(n)
    val known = Map(2->1, 3->10, 4->2, 12->6, 19->58)
      // Test with hints to show if it goes wrong
    for (n,s) <- known do withClue(s"for input n=$n,") { next(n) shouldBe s }
  }
    
  "Total stopping time" should "be 0 for base case n = 1" in {
    totalStoppingTimeOf(1) shouldBe 0
  }

  it should "at least be correct for a small set of other known values" in {
      // Known stopping times s for input n: n -> s
      val known = Map(2->1, 3->7, 4->2, 12->9, 19->20)
      // Test with hints to show if it goes wrong
      for (n,s) <- known do withClue(s"for input n=$n,") { totalStoppingTimeOf(n) shouldBe s }
  }

  "maximumInOrbitOf" should "be 1 for base case n = 1" in {
    maximumInOrbitOf(1) shouldBe 1
  }
  
  it should "at least be correct for a small set of other known values" in {
      // Some known maxima m for input n->m
      val known = Map(7->52, 6->16, 5->16, 4->4, 3->16, 2->2)
      // Test with hints to show if it goes wrong
      for (n,s) <- known do withClue(s"for input n=$n,") { maximumInOrbitOf(n) shouldBe s }
  }

end CollatzSpec
