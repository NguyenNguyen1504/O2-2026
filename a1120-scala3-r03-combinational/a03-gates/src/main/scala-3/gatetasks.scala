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

import tinylog._

object factory:
  /** 
   Returns a gate containing the eXclusive OR of the two input gates
   */
  def buildXOR(in1 : Gate, in2: Gate) : Gate =
    // TASK 1: Return a gate which contains the exclusive or (XOR) of the two
    //         input gates.
    // Hints:  - Remember that gates can be combined to create new gates using 
    //           logic operators || and && and negated using !
    //         - You can NOT check/compare the .value method of the gates as
    //           values are not set at the time you 'connect' the gates
    //           (remember that you are building a [very small] circuit)

    // Write your formula here:
    ((in1 && Gate.True) && !(in2 || Gate.False)) || ((in2 && Gate.True) && !(in1 || Gate.False))
    //  t <=> in1 t             f <=> in2 f
  end buildXOR

  /** 
    * Returns the majority state of gates in1, in2, in3
    */
  def buildMajorityOfThree(in1 : Gate, in2 : Gate, in3 : Gate) : Gate =
    // TASK 2: Return a gate which evaluates to
    // false - if two or three are false
    // true  - if two or three are true
    //
    // Hint: You are looking for a logic expression involving the three gates
    //       As always it will help to write the truth table or draw a diagram
    //       first

    // Write your formula here:
    (in1 && in2) || (in2 && in3) || (in3 && in1)
  end buildMajorityOfThree
  // a  b  c  s
  // 1  1  1  1
  // 1  1  0  1
  // 1  0  1  1
  // 0  1  1  1
  // 1  0  0  0
  // 0  1  0  0
  // 0  0  1  0
  // 0  0  0  0