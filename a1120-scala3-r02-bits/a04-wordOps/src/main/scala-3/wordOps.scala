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
 * Assignment:  Word operations
 *
 * Description:
 * This assignment asks you to implement common word operations that are
 * not available in the Scala programming language. The intent is to practice
 * your skills at working with bits.
 *
 */

package wordOps

/*
 * Task 1: Population count (number of 1-bits in a word)
 *
 * Complete the following function that takes as parameter
 * a 32-bit word, w, and returns __the number of 1-bits__ in w.
 *
 */

def popCount(w: Int): Int = ???

/*
 * Task 2: Reverse bit positions
 *
 * Complete the following function that takes as parameter
 * a 16-bit word, w, and returns a 16-bit word, r, such that
 * for every j=0,1,...,15,
 * the value of the bit at position j in r is equal to
 * the value of the bit at position 15-j in w.
 *
 */

def reverse(w: Short): Short = ???

/*
 * Task 3: Left rotation
 *
 * Complete the following function that takes two parameters
 *
 * 1) a 64-bit word, w, and
 * 2) a 32-bit word, k.
 *
 * The function returns a 64-bit word, r, such that
 * for all j=0,1,...,63
 * the value of the bit at position (j+k)%64 in r is equal to
 * the value of the bit at position j in w.
 *
 * Note that the value of k can also be negative.
 */

def leftRotate(w: Long, k: Int): Long = ???
