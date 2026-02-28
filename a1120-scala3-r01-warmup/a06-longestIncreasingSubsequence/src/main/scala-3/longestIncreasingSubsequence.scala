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
 * Assignment: Longest increasing subsequence
 *
 * Sequences are a natural source of computational problems. One such
 * family of problems involves finding subsequences with specified
 * properties in a given sequence. This exercise asks you to write 
 * a program that, given a sequence
 *
 *   s(0), s(1), ..., s(n-1)
 *
 * of integers as input, finds a ___longest increasing subsequence___ 
 * of the sequence s.
 * 
 * For example, suppose we are given as input the sequence
 * 
 *    72, 16, 51, 17, 6, 21, 92, 59, 54, 78, 41, 33, 94, 
 *        85, 83, 56, 2, 46, 57, 44, 73, 6, 47, 47, 0.
 *
 * In this sequence, a longest increasing subsequence has length 7. 
 * One example of such an increasing subsequence is 
 *
 *    16 < 17 < 21 < 54 < 56 < 57 < 73.
 *
 * More generally, your program must be such that 
 * given a sequence
 *
 *   s(0), s(1), ..., s(n-1)
 *
 * of integers as input, the program returns a subsequence 
 *
 *   s(i_1), s(i_2), ..., s(i_k)
 *
 * that meets all of the following three properties:
 * 
 *   1. The positions that define the subsequence are increasing:
 *
 *        0 <= i_1 < i_2 < ... < i_k <= n-1
 *
 *   2. The subsequence is increasing:
 *
 *        s(i_1) < s(i_2) < ... < s(i_k)
 *
 *   3. The length k of the subsequence is as large as possible.
 *
 *
 * Hints:
 *
 * You can solve this exercise using __dynamic programming__ as a 
 * solution strategy. That is, you __tabulate__ solutions of carefully
 * designed subproblems and use these solutions to gradually build your
 * way up to solve larger and larger subproblems until you solve 
 * the original problem. Here are some more detailed hints:
 *
 * a) Suppose that for all i = 0, 1, ..., j-1 we know L(i), the length 
 *    of a longest increasing subsequence in s(0), s(1), ..., s(i)
 *    that terminates at position i.
 * b) Suppose we have already computed in an array the values
 *    L(0), L(1), ..., L(j-1). How can we make use of these values 
 *    to compute the next value L(j) ? 
 * c) Furthermore, suppose for each i we know p(i), the position
 *    prior to the last position i in a longest increasing subsequence 
 *    that terminates at position i.
 *    (Note that such a position p(i) need not always exist, however.)
 * d) How can we make use of the values p(i) for i <= j to determine
 *    a concrete increasing subsequence of length L(j) 
 *    that terminates at j ?
 * e) Design your program to complete the arrays L and p, one position 
 *    at a time.
 * f) Use the arrays L and p to return a concrete longest increasing
 *    subsequence in the given sequence s.
 *
 */

package longestIncreasingSubsequence

  /** Returns a longest increasing subsequence in the sequence s.
   *  If there are multiple such subsequences, any subsequence will do. */

  def longestIncreasingSubsequence(s: Seq[Int]): Seq[Int] =
    val n = s.length
    require(n > 0)
//    pseudocode: // sub_i is the longest increasing subsequence that stops at i
//
//    L_i_array = []  // store the lengths of sub_is
//    p_i_array = []  // store the index of the number right before s_i in that longest subsequence sub_i
//    for every i from 0 to s.length -1:
//        for j from 0 to i-1:
  //        if s[j] < s[i] => L_i = L_j + 1 and p_i = j
//    find max from L_i_array: L_k => the longest subsequence is sub_k => position of the 2nd-to-last element of sub_k in the original list s
//

    val L = Array.fill(n)(0)
    val p = Array.fill(n)(0)
    for i <- 0 until n do
      var Lmax = 1
      var pmax = -1
      for j <- 0 until i do
        if s(j) < s(i) && L(j) + 1 > Lmax then
          Lmax = L(j) + 1
          pmax = j
      end for
      L(i) = Lmax
      p(i) = pmax
    end for
    var maxLength = 0
    var bestEnd = 0
    for i <- 0 until n do
      if L(i) > maxLength then
        maxLength = L(i)
        bestEnd = i
    end for

    var result = List[Int]()
    var current = bestEnd
    while current != -1 do
      result = s(current) :: result // Thêm phần tử vào đầu danh sách
      current = p(current)

    result.toSeq

  end longestIncreasingSubsequence

