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
 * A workout with sequences. (16 tasks, each a line or so of code.)
 *
 * This exercise rehearses your skills in working with
 * sequences in Scala. It may be useful to look at
 * the documentation of the Sequence (Seq) API:
 *
 * http://www.scala-lang.org/api/3.3.7/scala/collection/Seq.html
 *
 * Your tasks are given below.
 *
 */

package sequences

  /*
   * Task 1:
   * Practice with _mapping_ a function over a sequence.
   *
   * Define a function that adds one (+1) to every element
   * of a given sequence ('in') of integers and returns the resulting
   * sequence.
   *
   * Hints:
   * You may want to 'map' over the sequence a function that adds one.
   * You may define the function that adds one either explicitly,
   * or practice the use of anonymous functions.
   *
   */

  def addOneToSeq(in: Seq[Int]): Seq[Int] = in.map( _+1 )

  /*
   * Task 2:
   * Practice with _filtering_ a sequence.
   *
   * Define a function that returns a sequence where
   * all __odd__ integers from the input sequence have been deleted.
   *
   * Hints:

   * Recall that a POSITIVE integer x is odd if and only if ((x%2)==1) is true.
   * For negative integers one must take into account that modulo operator %
   * in scala returns the SIGNED remainder, and therefore (-3)%2 == -1.
   * For more information see https://en.wikipedia.org/wiki/Modulo.
   * 
   * You may want to 'filter' the sequence with a function that
   * returns 'true' if and only if the input integer is not odd
   * (i.e. the integer is even). This is more straightforward since
   * there is no remainder (i.e. it is 0) and the sign is not a problem.
   * 
   * You may define the function that tests for even-ness
   * either explicitly, or practice the use of anonymous functions.

   *
   */

  def removeOdd(in: Seq[Int]): Seq[Int] = in.filter( _ % 2 == 0)

  /*
   * Task 3:
   * Practice with _reducing_ a sequence using a binary
   * operator that satisfies the associative property,
   * such as '+', '*', 'min', 'max', sequence concatenation ('++'),
   * and so forth.
   *
   * http://en.wikipedia.org/wiki/Associative_property
   *
   * Here we practice reduction with '+', that is, we want
   * to take the _sum_ of the elements of a sequence.
   *
   * Your task is to define a function that returns the sum of
   * the integers in the (nonempty) input sequence.
   *
   * Remark:
   * Please do not use the library method 'sum' to solve this task.
   * Rather you should practice using the method 'reduce' with
   * a function supplied by you.
   *
   */

  def sumOfTwo(a: Int, b: Int) = a + b
  def mySum(in: Seq[Int]): Int = in.reduce(sumOfTwo(_,_))

  /*
   * Task 4:
   * Practice with a _cascade_ of transformations.
   *
   * Here is where things start getting more interesting.
   *
   * Define a function that returns the sum of the squares of
   * the odd integers in the input.
   * (You may assume that there is at least one odd integer in the input.)
   *
   * Hint:
   * Let us think about how to do this by applying a _cascade_ of
   * transformations, one after another. Perhaps we would want to
   *
   *  (1) first, filter the even integers out so that only odd integers remain,
   *  (2) then, map each remaining integer to its square, and
   *  (3) finally, reduce by taking the sum of the squares.
   *
   * We can follow this idea directly in Scala.
   * All you need to do below is to implement the inner
   * functions that effect (1,2,3) using filter, map, and reduce.
   *
   */

  def sumOfSquaresOfOdd(in: Seq[Int]): Int =
    in.filter(_ % 2 != 0)         // (1)  [when making a cascade it is convenient ..]
      .map(x => x*x)            // (2)  [.. to break the cascade into multiple]
      .reduce( _+_ )         // (3)  [.. lines to ease readability]

  /*
   * Task 5:
   * Now it is up to you to define the cascade from scratch.
   *
   * Define a function that returns the product of all
   * nonzero integers in the input.
   * (You may assume that the input has at least one nonzero integer.)
   *
   * Hint:
   * See the task above.
   *
   */

  def productOfNonzero(in: Seq[Int]) =
    in.filter(_ != 0).reduce(_*_)

  /*
   * Task 6:
   * One more cascade.
   *
   * Define a function that returns the minimum of the squares,
   * among the squares that are at least 17, of the positive integers
   * in the input. (You may assume that the input has at least one
   * positive integer whose square is at least 17.)
   *
   * Hint:
   * See the tasks above.
   *
   */

  def minOfSquaresAtLeast17OfPositive(in: Seq[Int]): Int =
    in.filter(_>0).map(x => x*x).filter(_>=17).min

  /*
   * Task 7:
   * Let us now practice working with sequences of __pairs__.
   *
   * So far we have worked with sequences of integers. Let us
   * now play a bit with sequences that consist of __pairs__ of integers.
   *
   * Recall that in Scala a pair is an object x of type Tuple2[A,B],
   * or more succinctly, type (A,B) , where A and B are the types of
   * the components of x.
   *
   * For example, if we are working with a pair x of integers
   * both with type Int, the pair x has type Tuple2[Int,Int],
   * or more succinctly, (Int,Int) .
   *
   * If x is a pair, we can get the first position of x
   * (e.g. the first integer in x) by writing
   *
   *      x._1
   *
   * Similarly, we can get the second position of x by writing
   *
   *      x._2
   *
   * An alternative way to handle pairs and tuples in general is
   * to use pattern matching, see https://docs.scala-lang.org/tour/tuples.html.
   * Basically, if we have a class called Pixel with a method .getRGB() which
   * returns the RGB value of the pixel as one tuple of 3 Ints, we could write
   * code in the following way:
   *
   *    val pix: Pixel = ...        // a Pixel object (defined by some class)
   *    val rgbTuple = pix.getRGB() // Get the red, green, and blue colours as a tuple (r,g,b)
   *    val red = rgbTuple._1       // separate red
   *    val green = rgbTuple._2     // separate green
   *    val blue = rgbTuple._3      // separate blue
   * 
   * Bit, this takes 4 rows to get the components separated nicely if we want to
   * avoid using the rgbTuple._X in the long run. A more succinct way to write this
   * is the following:
   *
   *    val pix: Pixel = ...        // a Pixel variable pix has been defined in some way
   *    val (red, green, blue) = pix.getRGB()   // assign each color to different variables immediately
   * 
   * When working with sequences using higher order functions (like .map())
   * we can also take advantage of pattern mathing. If we have a Seq of
   * the previously discussed rgb values
   * 
   *    val seq: Seq[(Int, Int, Int))] = ...
   * 
   * and we want to map it to a sequence of just the green values,
   * we can do any of the following:
   *
   *    seq.map(_._2)
   *    seq.map(rgb => rgb._2)
   *    seq.map((_,g,_) => g)
   * 
   * 
   * So suppose now we are given as input a __sequence__ of
   * pairs of integers (that is, an object of type Seq[Tuple2[Int,Int]],
   * or more succinctly, Seq[(Int,Int)] ).
   *
   * Your task is to define a function that returns a sequence consisting
   * of the second positions of the pairs whose first position is even.
   *
   * Hint:
   * Again you may want to use a cascade.
   * First, filter based on the first position,
   * then, map with a function that returns the second position of a pair.
   *
   */

  def sequenceOfSecondPartsWhoseFirstPartIsEven(in: Seq[(Int,Int)]): Seq[Int] =
    in.filter(pair => pair._1 % 2 == 0).map(_._2)
  /*
   * Task 8:
   * Let us continue working with sequences of pairs.
   *
   * Define a function that computes the product of each pair of integers
   * in the input sequence, and then takes the sum of these products.
   * (You may assume at least one pair is given as input.)
   *
   * Hint:
   * A cascade of a map followed by a reduce suffices.
   *
   */

  def sumOfProductsOfPairs(in: Seq[(Int,Int)]): Int =
    in.map(pair => pair._1 * pair._2).reduce(_+_)

  /*
   * Task 9:
   * Here we learn the useful idiom of _zipping_ two given sequences
   * (with the same length) into one sequence of pairs.
   *
   * In short, it often happens that you have two sequences, a and b,
   * and you need to do something to the elements in matching
   * positions in those sequences. That is, you want to work with
   * the elements a(i) and b(i) for each position
   * i=0,1,...,min(a.length,b.length)-1
   *
   * Clearly this is a situation where one would like to use a 'map',
   * but unfortunately map only works on the elements of one sequence.
   * The solution is to __zip__ the sequences a and b to produce a
   * sequence consisting of the pairs (a(i),b(i)) for each i.
   * To zip a with b, we call the 'zip' method of a:
   *
   *    a.zip(b)
   *
   * or what is the same using operator syntax,
   *
   *    a zip b
   *
   * The result is a sequence z such that z(i) is the pair (a(i),b(i))
   * for all i=0,1,...,min(a.length,b.length)-1.
   *
   * All right. So let us now practice zipping.
   *
   * Let us start with something really simple. Suppose we have
   * two sequences of integers, a and b, both of length d.
   * We may view these sequences as d-dimensional vectors.
   * So how do we compute the sum of these two vectors?
   * Clearly, the sum s = a + b should satisfy s(i) == a(i) + b(i)
   * for each i = 0,1,...,d-1.
   *
   * So why not 'zip' the two sequences, to obtain a sequence
   * of pairs (a(i),b(i)). Then, to form the sum vector,
   * all we need is a 'map' that sums up each pair ...
   *
   */

  def sumOfTwoDDimVectors(a: Seq[Int], b: Seq[Int]): Seq[Int] =
    (a zip b).map(pair => pair._1 + pair._2)

  /*
   * Task 10:
   * More practice with zipping.
   * What about the inner product of two d-dimensional vectors?
   * That is, the sum of a(i)*b(i) over i = 0,1,...,d-1.
   *
   */

  def innerProductOfTwoDDimVectors(a: Seq[Int], b: Seq[Int]): Int =
    (a zip b).map(pair => pair._1 * pair._2).reduce(_+_)

  /*
   * Task 11:
   * Yet more practice with zipping.
   * Suppose a and b are points in d-dimensional Euclidean space.
   * What is the square of the Euclidean distance between the points ?
   *
   * Hint:
   * Zip, take differences, square, sum up.
   *
   */

  def squareOfDDimEuclideanDistance(a: Seq[Int], b: Seq[Int]): Int =
    (a zip b).map(pair => pair._2 - pair._1).map(x => x*x).reduce(_+_)

  /*
   * Task 12:
   * Taking and dropping combined with zipping.
   *
   * Sequences have two useful methods, take(j: Int) and drop(j: Int),
   * that take or drop the _first_ (=lowest-numbered) j positions
   * of the sequence and return the resulting sequence.
   * There are also dual methods, takeRight(j: Int) and dropRight(j: Int),
   * that take or drop the _last_ (=highest-numbered) j positions
   * of the sequence and return the resulting sequence.
   *
   * Recall now that 'zip' pairs up matching positions a(i) and b(i)
   * into one sequence of pairs. Often we would like to pair up
   * sequences so that the sequences offset relative to one another
   * by, say, j positions. In more precise terms,
   * suppose that the sequences a and b both have the same length d.
   * Suppose we want to pair up a(i) with b(i+j) for each
   * i = 0,1,...,d-1-j. Define a function that carries out such
   * pairing between elements of a and b, and returns the resulting
   * sequence of pairs. Note that the resulting sequence of pairs
   * is of length d - j.
   *
   * Hint:
   * Either take or drop (or both) before zipping.
   *
   */

  def pairWithOffset(a: Seq[Int], b: Seq[Int], j: Int): Seq[(Int,Int)] =
    require(a.length == b.length && j >= 0 && j < a.length)
    a.zip(b.drop(j))

  /*
   * Task 13:
   * Pairs of consecutive elements.
   *
   * Yet another useful idiom is to pair up elements at consecutively
   * numbered positions in a sequence. That is, for a sequence z,
   * we want to form the sequence consisting of the pairs (z(i),z(i+1))
   * for i = 0,1,...,z.length-2 .
   *
   * Hint:
   * The previous task helps here. But maybe you can be more concise?
   *
   */

  def consecutivePositionPairs(z: Seq[Int]): Seq[(Int,Int)] =
    pairWithOffset(z, z, 1)

  /*
   * Task 14:
   * Zipping with index.
   *
   * Often it is necessary to work with the elements of a sequence
   * (e.g. using a 'map') in such a way that we have access to
   * the position where the element occurs. In such cases it is useful
   * to first zip the original sequence with a sequence of its indices.
   * That is, each element in the sequence gets paired
   * with its index (its position number) in the sequence.
   * While we could do this with a quick idiom such as
   *
   *     a zip (0 until a.length)
   *
   * there is in fact a separate method in class Seq that does precisely
   * this, namely 'zipWithIndex', so precisely the same what is above
   * is achieved more succinctly by
   *
   *     a.zipWithIndex
   *
   * Let us now look at an example why zipping with the index is useful.
   * Suppose you have a sequence of integers and you need to find
   * the index where the (first occurrence of the) maximum value in the
   * sequence occurs. That is, if a(i) is the maximum of a, we need to
   * find out i for the minimum such i.
   * Implement a method that uses zipWithIndex to find the first
   * index with the maximum value in the sequence a.
   *
   * Remark:
   * There is also a method available for finding the first index
   * of a maximum. However, you should not solve this exercise
   * by calling that method.
   *
   */

  def firstMaxPos(a: Seq[Int]): Int =
    a.zipWithIndex.reduce( (pair1, pair2) => if pair1._1 > pair2._1 then pair1 else pair2)._2

  /*
   * Task 15:
   * Unzipping.
   *
   * Given all this practice with zipping, one might wonder whether
   * it makes sense to sometimes 'unzip' as well. Perhaps not surprisingly,
   * it does, and there is a method 'unzip' available for such situations.
   *
   * One example is as follows. A key operation in the Haar wavelet transform
   * is to take two sequences a and b as input, and produce a pair of
   * two sequences as output, namely the elementwise (vector) sum a+b and
   * the elementwise (vector) difference a-b.
   *
   * Your task is to define a function that implements this operation
   * on two given sequences.
   *
   * Hint:
   * Zip, map (to produce sum and difference), and finally unzip.
   *
   * Remark:
   * Again there are many ways to solve this task, perhaps you
   * would like to practice unzipping here.
   *
   * Remark 2:
   * For more on the Haar transform, see
   * http://en.wikipedia.org/wiki/Haar_wavelet
   *
   */

  def sumAndDifferenceSeqs(a: Seq[Int], b: Seq[Int]): (Seq[Int],Seq[Int]) =
    a.zip(b).map(pair => ((pair._1 + pair._2),(pair._1 - pair._2))).unzip

  /*
   * Task 16:
   * Fold.
   *
   * When using the _reduce_ operation we have to be careful that
   * the sequence being reduced is not empty, or otherwise an
   * exception results.
   *
   * The _fold_ operation is otherwise like reduce, but we get to
   * specify a _neutral element_ for the associative binary operator that
   * we use for reduction. (Examples of neutral elements are 0 for addition,
   * 1 for multiplication, and the empty string ("") for string concatenation.)
   * Fold returns this neutral element if the input sequence is empty.
   *
   * Your task is to use 'fold' to define a function that
   * concatenates all the strings in a given sequence of strings
   * into one string. If the sequence is empty, the empty string
   * must be returned.
   *
   */

  def stringsConcatenated(in: Seq[String]): String =
    in.fold("")(_+_)



