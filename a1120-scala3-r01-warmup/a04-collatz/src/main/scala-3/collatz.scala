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


/** 
* The following assumes that the Collatz conjecture holds
* See https://en.wikipedia.org/wiki/Collatz_conjecture
*/
package collatz

// TASK 1
/**
* Given a value n this function should return the next value in the sequence.
* That is, next implements the function f such that f(n) is n/2 if n is even, and 3 x n + 1 otherwise.
* Note: TASK 1 is not a recursive function, only a simple expression.
*/
def next(n: Int): Int =
  // Check that n is a positive number and that it would not overflow
  require(n > 0 && n < ((Int.MaxValue - 1)/3), s"n = $n is not valid")
  if n % 2 == 0 then n/2 else 3 * n + 1
end next

// TASK 2
/**
* Returns the total stopping time for a positive
* value n.
* That is, the first k such that a_k == 1, when a_0 = n and a_k+1 = f(a_k).
*/
def totalStoppingTimeOf(n: Int): Int =
  require(n > 0, s"n = $n is not > 0")
  if n == 1 then 0
  else
    totalStoppingTimeOf(next(n)) + 1
end totalStoppingTimeOf

// TASK 3
/**
* Returns the maximum value encountered in a specific Collatz orbit.
* That is, the maximum value in the sequence a_0, a_1, ... a_k,
* where a_0 = n, a_k == 1, and a_k+1 = f(a_k).
*/
def maximumInOrbitOf(n: Int): Int =
  require(n > 0, s"n = $n is not > 0")
  if n == 1 then 1
  else
    math.max(n, maximumInOrbitOf(next(n)))
end maximumInOrbitOf
