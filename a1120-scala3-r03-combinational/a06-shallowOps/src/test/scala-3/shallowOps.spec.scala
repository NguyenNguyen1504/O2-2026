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


package shallowOps

import tinylog._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class shallowOpsTest extends AnyFlatSpec with Matchers:
    /**
   * A small helper function for setting values in a bus consisting only 
   * of input elements. The values are taken from the bus.length least 
   * significant bits of the unsigned base-2 presentation of v.
   */
  def setBits(bus: Bus, v: BigInt) = {
    require(v >= 0, "v should not be negative, it is interpreted as a base-2 unsigned integer")
    bus.foreach(g => require(g.isInstanceOf[InputElement], 
                             "The bus must consist of input elements only"))
    var i = 0
    var t = v
    while i < bus.length do {
      bus(i).asInstanceOf[InputElement].set(t % 2 == 1)
      t = t / 2
      i += 1
    }
  }

  /**
   * Make a Boolean sequence of length n from the n least significant 
   * bits of the unsigned base-2 presentation of v.
   */
  def toBooleanSeq(n: Int, v: BigInt) = {
    require(n > 0, "n should be positive")
    require(v >= 0, "v should not be negative, it is interpreted as a base-2 unsigned integer")
    val a = new Array[Boolean](n)
    var i = 0
    var t = v
    while i < n do {
      a(i) = (t % 2 == 1)
      t = t / 2
      i += 1
    }
    a.toIndexedSeq
  }

  /**
   * Helper to calculate base-2 logarithms (rounded up)
  */
  def log2ceil(i: Int) = {
      assert(i > 0, s"Cannot calculate the logarithm of $i")
      var v = 0
      var t = i - 1
      while t != 0 do
          v += 1
          t = t >> 1
      v
  }

  "buildShallowOr" should "return the bus with the correct depth" in {
      val n = 64
      val a = Bus.inputs(n)
      val out = factory.buildShallowOr(a)
      val depth = out.depth
      val maxDepth = log2ceil(n)

      withClue("With n = %d,".format(n)) {
          depth should be <= maxDepth
      }
  }

  "buildShallowOr" should "return the bus with the correct size" in {
      val n = 64
      val a = Bus.inputs(n)
      val out = factory.buildShallowOr(a)
      val size = out.nofReferenced
      val maxSize = 2 * n - 1

      withClue("With n = %d, ".format(n)) {
          size should be <= maxSize
      }
  }

  "buildShallowOr" should "return the correct value" in {
    val a = Bus.inputs(64)
    val out = factory.buildShallowOr(a)
    setBits(a, 0x1020304057ffffffL)
    var expected = a.foldLeft(Gate.False)((result, gate) => result || gate).value
    withClue("With a = 0x1020304057ffffffL, ") {
      out.value shouldBe expected
    }
    setBits(a, 0x0L)
    expected = a.foldLeft(Gate.False)((result, gate) => result || gate).value
    withClue("With a = 0x0L, ") {
      out.value shouldBe expected
    }
  }

  "buildShallowIncrementer" should "have the correct depth" in {
    val n = 64
    val a = Bus.inputs(n)
    val out = factory.buildShallowIncrementer(a)
    val depth = out.map(_.depth).max
    val maxDepth = 4 * log2ceil(n)
    withClue("With n = %d, ".format(n)) {
      depth should be <= maxDepth
    }
  }

  "buildShallowIncrementer" should "have the correct size" in {
    val n = 64
    val a = Bus.inputs(n)
    val out = factory.buildShallowIncrementer(a)
    val size = out.nofGates
    val maxSize = 10 * n * log2ceil(n)
    withClue("With n = %d, ".format(n)) {
      size should be <= maxSize
    }
  }

  "buildShallowIncrementer" should "return the correct bus for a 4-input bus" in {
    // Input bus a
    val a = Bus.inputs(4)
    // Incrementer
    val out = factory.buildShallowIncrementer(a)
    // Set aa to 1011, i.e. the binary encoded number 11
    setBits(a, 11)
    // We expect the answer to be 12, i.e. 1100
    val expected = toBooleanSeq(4, 12)
    withClue("a = Bus(%b, %b, %b, %b), ".format(a(0).value, a(1).value, a(2).value, a(3).value)) {
      out.values should equal (expected)
    }
  }

  "buildShallowIncrementer" should "return the correct bus for a 64-input bus" in {
    val a = Bus.inputs(64)
    val out = factory.buildShallowIncrementer(a)
    setBits(a, 0x1020304057ffffffL)
    val expected = toBooleanSeq(64, 0x1020304058000000L)
    withClue("With a = 0x1020304057ffffffL, ") {
      out.values should equal (expected)
    }
  }

  "buildAdder" should "return the correct bus" in {
    val a = Bus.inputs(4)
    val b = Bus.inputs(4)
    val out = factory.buildAdder(a, b)
    // Set a to 0110, i.e. the signed binary encoded number 6
    setBits(a, 6)
    // Set b to 1000, i.e. the unsigned binary encoded number 8
    setBits(b, 8)
    // We expect the answer to be 14 (1110 in unsigned binary with 4 bits)
    val expected = toBooleanSeq(4, 14)
    withClue("With a = Bus(%b, %b, %b, %b) and b = Bus(%b, %b, %b, %b), "
      .format(a(0).value, a(1).value, a(2).value, a(3).value, b(0).value, b(1).value, b(2).value, b(3).value)) {
        out.values should equal (expected)
    }
  }

  "buildShallowAdder" should "have the correct depth" in {
    val n = 64
    val a = Bus.inputs(n)
    val b = Bus.inputs(n)
    // Build the adder
    val out = factory.buildShallowAdder(a, b)
    val depth = out.map(_.depth).max
    val maxDepth = 6 * log2ceil(n)
    withClue("With n_a = %d and n_b = %d, ".format(n, n)) {
      depth should be <= maxDepth
    }
  }

  "buildShallowAdder" should "have the correct size" in {
    val n = 64
    val a = Bus.inputs(n)
    val b = Bus.inputs(n)
    // Build the adder
    val out = factory.buildShallowAdder(a, b)
    val size = out.nofGates
    val maxSize = 30 * n * log2ceil(n)
    withClue("With n_a = %d and n_b = %d, ".format(n, n)) {
      size should be <= maxSize
    }
  }

  "buildShallowAdder" should "return the correct bus for 4-bit inputs" in {
    val a = Bus.inputs(4)
    val b = Bus.inputs(4)
    val out = factory.buildShallowAdder(a, b)
    // Set a to 0110, i.e. the signed binary encoded number 6
    setBits(a, 6)
    // Set b to 1000, i.e. the unsigned binary encoded number 8
    setBits(b, 8)
    // We expect the answer to be 14 (1110 in unsigned binary with 4 bits)
    val expected = toBooleanSeq(4, 14)
    withClue("With a = 0x6 and b = 0x8, ") {
      out.values should equal (expected)
    }
  }

  "buildShallowAdder" should "return the correct bus for 64-bit inputs" in {
    val a = Bus.inputs(64)
    val b = Bus.inputs(64)
    // Build the adder
    val out = factory.buildShallowAdder(a, b)
    setBits(a, 0x0f0f0f0f0f0f0f0fL)
    setBits(b, 0x10203040010200f1L)
    val expected = toBooleanSeq(64, 0x1f2f3f4f10111000L)
    withClue("With a = 0x0f0f0f0f0f0f0f0fL and b = 0x10203040010200f1L, ") {
      out.values should equal (expected)
    }
  }

