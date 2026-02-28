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


/*
 * Description:
 * This assignment asks you to study the properties of the radix point
 * representation of rational numbers. In particular, the radix point
 * representation of every rational number decomposes into three
 * parts, each of which is a sequence of digits in the chosen base B:
 *
 * (a) the integer part
 * (b) the fractional part, which further decomposes into
 *     (b1) the finite transient
 *     (b2) the finite period (that repeats infinitely many times)
 *
 * This decomposition always exists and is unique if we choose the
 * shortest possible finite transient, the shortest possible finite period,
 * and require the period to be 0 whenever multiple choices exist for
 * the period. In what follows we assume that the decomposition is always
 * chosen in this way.
 *
 * Example 1:
 * Consider the rational number 45679/3700, which expands in base B = 10
 * radix point as           12.34567567567567...
 *
 * The integer part is      12
 * The finite transient is     34
 * The finite period is          567   (this repeats infinitely many times)
 *
 * Example 2:
 * Consider the rational number 23/1, which expands in base B = 10
 * radix point as           23.00000000000000...
 *
 * The integer part is      23
 * The finite transient is        (the transient is empty)
 * The finite period is        0  (this repeats infinitely many times)
 *
 * Example 3:
 * Consider the rational number 3/2, which expands in base B = 10
 * radix point as           1.500000000000000...
 *
 * The integer part is      1
 * The finite transient is    5
 * The finite period is        0  (this repeats infinitely many times)
 *
 */

package rationalDecompose

/*
 * Task:
 * Write a function that computes the decomposition of a given
 * nonnegative rational number p/q in a given base b. The decomposition
 * is to be returned in a three-tuple (integer, transient, period),
 * where each component of the tuple is a sequence (Seq)
 * of digits in base b, and each digit has type Int.
 *
 * Examples:
 * decompose(45679,3700,10) must return (Seq(1,2),Seq(3,4),Seq(5,6,7))
 * decompose(49,99,10)      must return (Seq(0),Seq[Int](),Seq(4,9))
 * decompose(3,2,10)        must return (Seq(1),Seq(5),Seq(0))
 *
 */

def decompose(p: Int, q: Int, b: Int): (Seq[Int], Seq[Int], Seq[Int]) =
  require(p >= 0 && q > 0 && p < 100000 && q < 100000 && b >= 2 && b <= 100)
  // you may assume p, q, b meet the above requirements
  ???
end decompose
