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


package lfsr

import minilog._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class lsfrSpec extends AnyFlatSpec with Matchers:
  "buildRotator" should "return the correct bus" in {
    val clocks = 100

    /** Create the circuit. */
    val circuit = new Circuit()

    val test_instances = Vector(
      (3, Seq(0, 1), "(0, 1, 1)", Seq(true, false, true), "(1, 0, 1)"),
      (
        5,
        Seq(0, 2, 3),
        "(0, 1, 1, 0, 1)",
        Seq(false, true, true, false, true),
        "(0, 1, 1, 0, 1)"
      ),
      (
        6,
        Seq(0, 1, 2),
        "(0, 0, 0, 1, 1, 1)",
        Seq(false, true, true, true, false, false),
        "(0, 1, 1, 1, 0, 0)"
      )
    )

    for (n, inputs, inputs_formatter, answers, answer_formatter) <-
        test_instances
    do
      val state = circuit.inputs(n)
      val rotator = factory.buildRotator(state)
      for input <- inputs do state(input).set(true)
      for t <- 0 until clocks do circuit.clock()
      withClue(
        "With initial states %s, the expected output after %d clocks is %s. "
          .format(inputs_formatter, clocks, answer_formatter)
      ) {
        rotator.values.reverse should equal(answers)
      }
  }

  "build5BitsLFSR" should "give the correct outputs" in {

    /** The number of times the circuit is clocked during the test. This LFSR
      * cycles through 31 distinct states from a non-zero initial state. There
      * are 31 distinct non-zero states, since there are 2^5-1 = 31 non-zero
      * 5-bit numbers.
      */
    val clocks = 31

    /** If the LFSR is initialized with a state that has value true in gate 0
      * and false in other gates, and if the outputs are taken from gate 0 at
      * each time instance, the following sequence should be obtained. The
      * sequence has period 31 since the LFSR has 31 distinct states.
      */
    val refOutputs =
      Seq(true, false, false, false, false, true, false, false, true, false,
        true, true, false, false, true, true, true, true, true, false, false,
        false, true, true, false, true, true, true, false, true, false)

    /** Build the LFSR and check that the output of the LFSR, i.e., gate 0, has
      * the correct value for times t = 0, 1, ..., 30. The values are checked
      * using the reference outputs.
      */
    val circuit = new Circuit()
    val state = circuit.inputs(5)
    val LFSR = factory.build5BitLFSR(state)
    state(0).set(true)
    for t <- 0 until clocks do
      withClue(
        "The output at time %d should be %b. ".format(t, refOutputs(t))
      ) {
        LFSR(0).value shouldBe refOutputs(t)
      }
      circuit.clock()
  }

  "buildLFSR" should "give the correct outputs" in {

    /** The number of times the circuit is clocked during the test. */
    val clocks = 100

    /** Create a new circuit instance. */
    val circuit = new Circuit()

    /** Build a maximal LFSR with length 4 using the tap set {1} and initial
      * state (0, 0, 0, 1).
      */
    val n = 4
    val state = circuit.inputs(n)
    val taps = List(1)
    val LFSR = factory.buildLFSR(taps, state)
    state(0).set(true)

    /** The following output sequence with period 15 should be obtained from the
      * LFSR in this case.
      */
    val period = 15
    val refOutputs =
      Seq(true, false, false, false, true, false, false, true, true, false,
        true, false, true, true, true)

    /** Check the values using the reference outputs. */
    for t <- 0 until clocks do {
      withClue(
        "The output at time %d should be %b. ".format(t, refOutputs(t % period))
      ) {
        LFSR(0).value shouldBe refOutputs(t % period)
      }
      circuit.clock()
    }
  }

  it should "allow an empty set of taps" in {
    val circuit = new Circuit()
    val state = circuit.inputs(4)
    val taps = List()
    val lvsr = factory.buildLFSR(taps, state)
  }
