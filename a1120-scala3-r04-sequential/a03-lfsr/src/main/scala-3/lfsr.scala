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

object factory:

  /** Rotator unit.
    *
    * @param state
    *   the state elements of the rotator (to be constructed)
    *
    * @return
    *   the state elements of the rotator (with feedbacks built)
    *
    * Let us start this assignment with an easy task. You are to build a
    * rotation of feedbacks on a bus of state elements that you get as input.
    *
    * In somewhat more precise terms, when the clock triggers, it must be the
    * case that
    *
    * state(0) feeds back to state(state.length-1), state(1) feeds back to
    * state(0), state(2) feeds back to state(1), state(3) feeds back to
    * state(2),
    *
    * ..., and so forth, until finally,
    *
    * state(state.length-1) feeds back to state(state.length-2).
    *
    * Remark: A rotator can be used to cycle through a sequence of bits loaded
    * to the state elements. In fact, if you think about the oscillator
    * assignment, this is precisely what we did in that assignment. Rotators can
    * be used to build, e.g. a rotating text display by using one rotator to
    * drive one line of display elements such as LEDs.
    */
  def buildRotator(state: Bus): Bus =
    require(state.length > 0, "Bus width must be a positive integer.")

    val length = state.length
    for i <- 0 until length-1 do
      state(i).buildFeedbackFrom(state(i+1))
    state(length-1).buildFeedbackFrom(state(0))

    state(0).set(true)
    state.toSeq
  end buildRotator

  /** A 5-bit linear-feedback shift register (a 5-bit LFSR).
    *
    * In many applications, in particular in cryptography, it is useful to be
    * able to cycle through a long sequence of distinct states using only very
    * little hardware to do so.
    *
    * A ___linear-feedback shift register___ (an __LFSR___) is a sequential
    * logic circuit with b bits of internal state that can be used to cycle
    * through a sequence of up to 2^b-1 states. For example, with b = 100 we can
    * cycle through a sequence of 2^100-1 = 1267650600228229401496703205375
    * distinct states.
    *
    * An LFSR is in structure almost equivalent to a rotator. Indeed, with the
    * exception of the feedback to state(state.length-1), the feedbacks are
    * precisely the same. Namely, in an LFSR,
    *
    * state(1) feeds back to state(0), state(2) feeds back to state(1), state(3)
    * feeds back to state(2),
    *
    * ..., and so forth, until finally,
    *
    * state(state.length-1) feeds back to state(state.length-2).
    *
    * What makes an LFSR different from a rotator is the feedback to
    * state(state.length-1). Namely, this feedback is obtained from the
    * exclusive-or (the XOR) of two or more of the state bits. These bits are
    * called the __tap bits__ (or __taps__) for the LFSR.
    *
    * Depending on the tap bits chosen, we can obtain a cycle of up to 2^b-1
    * distinct states when the LFSR is initialized with a non-all-zero state. In
    * general to obtain the maximal length 2^b-1, the tap bits have to be very
    * carefully chosen.
    *
    * This task asks you to implement a maximal LFSR for 5 bits. In more precise
    * terms, the taps 0 and 2, that is, tapping the state elements state(0) and
    * state(2), will produce a sequence of 2^5-1 = 31 distinct states as the
    * clock is triggered.
    *
    * You will get as input a bus of 5 state elements, and your task is to build
    * the required feedbacks and the logic gates that implement a 5-bit LFSR
    * with taps 0 and 2. Furthermore, you should initialize (set) the state
    * elements so that state element 0 is set to true and all the other state
    * elements are false. Finally, you should give as return value the bus of
    * state elements.
    *
    * Hints: Test your design in Trigger (see the object "play" for example
    * code). If your design is correct, clocking the LFSR repeatedly will cycle
    * it through 31 distinct 5-bit states and then return back to the initial
    * state. You can also compare your design with the illustration in the
    * accompanying material. XOR gates are available in minilog via the operator
    * ^^.
    */
  def build5BitLFSR(state: Bus): Bus =
    require(state.length == 5, "Bus width must be five.")

    val length = state.length
    for i <- 0 until length-1 do
      state(i).buildFeedbackFrom(state(i+1))
    state(length-1).buildFeedbackFrom(state(0) ^^ state(2))

    state(0).set(true)
    state.toSeq
  end build5BitLFSR


  /** LFSR unit.
    *
    * @param taps
    *   the tap list that indicates which gates are providing feedback to
    *   state element b-1
    *
    * @param state
    *   the state elements for the LFSR
    *
    * @return
    *   a new LFSR that is built and initialized as instructed below
    *
    * This task asks you to implement a generic LFSR-builder function that
    * constructs the desired taps and feedbacks based on a list of taps and a
    * bus of state elements given as input. (A detailed description of LFSRs is
    * given in the comments above.)
    *
    * If the LFSR has b bits of state, the tap values in the list must be chosen
    * from {1, 2, ..., b-1}. That is to say, we assume that the state element 0
    * is always tapped (and it should not occur in the list). In particular, an
    * empty tap list should create a rotator unit.
    *
    * Your task is to write the builder function that builds the feedbacks and
    * taps. Unlike with the 5-bit LFSR above, the state of this LFSR is allowed
    * to be initialized so that any state element can be initially set to either
    * true or false.
    *
    * Hint: To test your code you can use the fact that for b = 3 both the tap
    * list {1} and the tap list {2} give LFSRs that cycle through all the
    * non-zero states. See the task above for further hints.
    */
  def buildLFSR(taps: List[Int], state: Bus): Bus =
    require(state.length > 0, "Bus width must be a positive integer.")
    require(
      taps.length == taps.distinct.length,
      "Tap list cannot contain duplicates."
    )
    require(taps.forall(_ > 0), "Tap values must be positive.")
    require(
      taps.forall(_ < state.length),
      "Tap values must be smaller than the width of the input bus."
    )

    val length = state.length
    for i <- 0 until length-1 do
      state(i).buildFeedbackFrom(state(i+1))
    state(length-1).buildFeedbackFrom(
      taps.foldLeft(state(0))( (accGate, tapIdx) =>
        val currentTapGate = state(tapIdx)
        accGate ^^ currentTapGate
      )
    )

    state
  end buildLFSR

end factory
