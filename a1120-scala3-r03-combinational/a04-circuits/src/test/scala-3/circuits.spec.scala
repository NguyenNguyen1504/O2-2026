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


package circuits

import tinylog._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class circuitTest extends AnyFlatSpec with Matchers:
  /** Generate a list of every number that can be expressed in `bits` bits */
  def generate(bits: Int) =
    val maxn = 1 << bits
    for num <- 0 until maxn
    yield (num, num.toBinaryString.reverse.padTo(bits, '0').map(_ == '1'))

  /** Set the value of each InputElement of a Bus */
  def setBus(bus: Bus, bits: Iterable[Boolean]) =
    bits.zipWithIndex
      .foreach((bit, idx) =>
        bus(idx) match
          case in: InputElement => in.set(bit)
          case _                => {}
      )

  "buildEqual" should "return the correct value" in {
    val aa = Bus.inputs(3)
    val bb = Bus.inputs(3)
    val out = factory.buildEqual(aa, bb)

    for
      (num1, bits1) <- generate(3)
      (num2, bits2) <- generate(3)
    do
      setBus(aa, bits1)
      setBus(bb, bits2)

      withClue(
        s"With bus1=${aa.values}, bus2=${bb.values} (evaluating $num1 == $num2)"
      ) {
        out.value shouldBe (num1 == num2)
      }
  }

  "buildUnsignedLess" should "return the correct value" in {
    val aa = Bus.inputs(3)
    val bb = Bus.inputs(3)
    val out = factory.buildUnsignedLess(aa, bb)

    for
      (num1, bits1) <- generate(3)
      (num2, bits2) <- generate(3)
    do
      setBus(aa, bits1)
      setBus(bb, bits2)

      withClue(
        s"With bus1=${aa.values}, bus2=${bb.values} (evaluating $num1 < $num2)"
      ) {
        out.value shouldBe (num1 < num2)
      }

  }

  "buildUnsignedLessOrEqual" should "return the correct value" in {
    val aa = Bus.inputs(3)
    val bb = Bus.inputs(3)
    val out = factory.buildUnsignedLessOrEqual(aa, bb)

    for
      (num1, bits1) <- generate(3)
      (num2, bits2) <- generate(3)
    do
      setBus(aa, bits1)
      setBus(bb, bits2)

      withClue(
        s"With bus1=${aa.values}, bus2=${bb.values} (evaluating $num1 <= $num2)"
      ) {
        out.value shouldBe (num1 <= num2)
      }
  }

  "buildIncrementer" should "return the correct bus" in {
    val a0 = new InputElement()
    val a1 = new InputElement()
    val a2 = new InputElement()
    val aa = Bus(a0, a1, a2)
    // Build the circuit
    val out = factory.buildIncrementer(aa)

    // Set aa to 011, i.e. the binary encoded number 3
    a0.set(true)
    a1.set(true)
    a2.set(false)
    // We expect the answer to be 4, i.e. 100
    val expected1 = Seq(false, false, true)
    withClue("With aa = Bus(true, true, false), ") {
      out.values should equal(expected1)
    }

    // Set aa to 111, i.e. the binary encoded number 7
    a0.set(true)
    a1.set(true)
    a2.set(true)
    // We expect the answer to be 7+1 mod 8 = 0, i.e. 000
    val expected2 = Seq(false, false, false)
    // Test
    withClue("With aa = Bus(true, true, true), ") {
      out.values should equal(expected2)
    }
  }

  "buildAdder" should "return the correct bus" in {
    val a0 = new InputElement()
    val a1 = new InputElement()
    val a2 = new InputElement()
    val aa = Bus(a0, a1, a2)
    val b0 = new InputElement()
    val b1 = new InputElement()
    val b2 = new InputElement()
    val bb = Bus(b0, b1, b2)
    // Build the circuit
    val out = factory.buildAdder(aa, bb)

    // Set aa to 111, i.e. the signed binary encoded number 7
    a0.set(true)
    a1.set(true)
    a2.set(true)
    // Set bb to 110, i.e. the unsigned binary encoded number 6
    b0.set(false)
    b1.set(true)
    b2.set(true)
    // We expect the answer to be 5
    // (13 mod 8, i.e. 101 in unsigned binary with 3 bits)
    val expected = Seq(true, false, true)
    // Test
    withClue("With aa = Bus(true, true, true), bb = Bus(false, true, true)") {
      out.values should equal(expected)
    }
  }

  "buildAdderNoOverflow" should "return the correct bus" in {
    val a0 = new InputElement()
    val a1 = new InputElement()
    val a2 = new InputElement()
    val aa = Bus(a0, a1, a2)
    val b0 = new InputElement()
    val b1 = new InputElement()
    val b2 = new InputElement()
    val b3 = new InputElement()
    val bb = Bus(b0, b1, b2, b3)
    // Build the circuit
    val out = factory.buildAdderNoOverflow(aa, bb)

    // Set aa to 111, i.e. the signed binary encoded number 7
    a0.set(true)
    a1.set(true)
    a2.set(true)
    // Set bb to 1100, i.e. the unsigned binary encoded number 12
    b0.set(false)
    b1.set(false)
    b2.set(true)
    b3.set(true)
    // We expect the answer to be 19 (10011 in unsigned binary with 5 bits)
    val expected = Seq(true, true, false, false, true)
    // Test
    withClue(
      "With aa = Bus(true, true, true), bb = bus(false, false, true, true), "
    ) {
      out.values should equal(expected)
    }
  }

  "buildCountTrueBits" should "return the correct number of true bits -- 1" in {
    val a0 = new InputElement()
    val a1 = new InputElement()
    val a2 = new InputElement()
    val aa = Bus(a0, a1, a2)
    // Build the circuit
    val out = factory.buildCountTrueBits(aa)

    // Set aa to 101
    a0.set(true)
    a1.set(false)
    a2.set(true)
    // We expect the answer to be 2, i.e. 10
    val expected = Seq(false, true)
    // Test
    withClue("With aa = Bus(true, false, true), ") {
      out.values should equal(expected)
    }
  }

  "buildCountTrueBits" should "return the correct number of true bits -- 2" in {
    val a0 = new InputElement()
    val a1 = new InputElement()
    val a2 = new InputElement()
    val a3 = new InputElement()
    val aa = Bus(a0, a1, a2, a3)
    // Build the circuit
    val out = factory.buildCountTrueBits(aa)

    // Set aa to 1111
    a0.set(true)
    a1.set(true)
    a2.set(true)
    a3.set(true)
    // We expect the answer to be 4, i.e. 100
    val expected = Seq(false, false, true)
    // Test
    withClue("With aa = Bus(true, true, true, true), ") {
      out.values should equal(expected)
    }
  }

  "buildMajorityTester" should "return the correct value" in {
    val aa = Bus.inputs(4)
    val out = factory.buildMajorityTester(aa)

    for (_, bits) <- generate(4) do
      setBus(aa, bits)
      withClue(s"With aa = ${aa.values}") {
        out.value shouldBe (bits.count(v => v) > 2)
      }
  }

  "buildMultiplier" should "return the correct bus" in {
    val a0 = new InputElement()
    val a1 = new InputElement()
    val a2 = new InputElement()
    val aa = Bus(a0, a1, a2)

    val b0 = new InputElement()
    val b1 = new InputElement()
    val b2 = new InputElement()
    val b3 = new InputElement()
    val bb = Bus(b0, b1, b2, b3)

    val c0 = new InputElement()
    val c1 = new InputElement()
    val c2 = new InputElement()
    val c3 = new InputElement()
    val cc = Bus(c0, c1, c2, c3)

    // Build the circuits
    val out = factory.buildMultiplier(aa, bb)
    val out2 = factory.buildMultiplier(aa, cc)
    val out3 = factory.buildMultiplier(bb, cc)

    // Set aa to 011, i.e. the unsigned binary encoded number 3
    a0.set(true)
    a1.set(true)
    a2.set(false)
    // Set bb to 1100, i.e. the unsigned binary encoded number 12
    b0.set(false)
    b1.set(false)
    b2.set(true)
    b3.set(true)
    // We expect the answer to be 36 (0100100 in unsigned binary with 7 bits)
    var expected = Seq(false, false, true, false, false, true, false)
    // Test
    withClue(
      "With aa = Bus(true, true, false) (3 in decimal)," +
        " bb = Bus(false, false, true, true) (12 in decimal)\n"
    ) {
      out.values should equal(expected)
    }

    // Set cc to 0001, i.e. the unsigned binary encoded number 1
    c0.set(true)
    // We expect the answer to be 3 (0000011 in unsigned binary with 7 bits)
    expected = Seq(true, true, false, false, false, false, false)
    // Test
    withClue(
      "With aa = Bus(true, true, false) (3 in decimal)," +
        " cc = Bus(true, false, false, false) (1 in decimal)\n"
    ) {
      out2.values should equal(expected)
    }

    // Set cc to 1001, i.e. the unsigned binary encoded number 9
    c3.set(true)
    // We expect the answer to be 108 (01101100 in unsigned binary with 8 bits)
    expected = Seq(false, false, true, true, false, true, true, false)
    // Test
    withClue(
      "With bb = Bus(false, false, true, true) (12 in decimal)," +
        " cc = Bus(true, false, false, true) (9 in decimal)\n"
    ) {
      out3.values should equal(expected)
    }
  }
