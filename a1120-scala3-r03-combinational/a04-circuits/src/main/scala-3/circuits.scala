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

object factory:

  /** Returns a gate that evaluates to true if and only if the corresponding
  * gates in the argument buses evaluate to the same values. That is, the gate
  * evaluates to true if bus1 and bus2 evaluate to the same bit sequence
  * (binary number).
  *
  * For instance, assume that aa and bb are buses with 3 bits. When aa
  * evaluates to 100 (4 in decimal) and bb to 110 (6 in decimal), then the
  * gate buildEqual(aa, bb) evaluates to false.
  *
  * Hints:
  *
  * (1)  First, for each i, you may want to build a gate that evaluates 
  *      to true if the gates bus1(i) and bus2(i) have the same value
  *      (that is, either both are false or both are true).
  *      You can do this either with, say, a while-loop over i or 
  *      with bus-level operations (the latter approach takes about
  *      half a line of Scala code).
  *      Also observe that the study material for Round 2 contains 
  *      an example of a small circuit that evaluates to true iff two 
  *      inputs have the same value. What you need to do is to build 
  *      this circuit repeatedly for each pair of matching gates, 
  *      bus1(i) and bus2(i).
  * (2)  Second, you need to make sure that all the gates you constructed
  *      in (1) have the value true. You may want to consider taking
  *      the AND of the gates, one two-input AND-gate at a time. That is, 
  *      you have a gate g whose output is the AND of the first j gates,
  *      and you obtain the AND of the first j+1 gates by constructing a
  *      two-input AND with g and the (j+1)th gate as inputs.
  *      Alternatively, you may, say, do a "reduceLeft" across the bus. 
  * (3)  A drawing of one possible circuit that you can build is available
  *      (in the case when bus1 and bus2 both have length 4) in the
  *      A+-page of this assignment, with parts (1) and (2) highlighted.
  *      To successfully solve this task, you only need to generalize to
  *      lengths other than 4.
  */
  def buildEqual(bus1: Bus, bus2: Bus): Gate =
    require(
      bus1.length == bus2.length,
      "Can only compare buses of the same length"
    )
    (bus1 zip bus2).map( (i1, i2) =>
      ((i1 && Gate.True) && (i2 && Gate.True)) || !((i1 || Gate.False) || (i2 || Gate.False)))
      .reduceLeft(_ && _)

  end buildEqual

  /**
  * The unsigned less-than comparator.
  * Returns a gate that evaluates to true if and only if
  * the evaluated value of bus1 is less than that of bus2
  * in unsigned binary.
  * The buses must be non-empty and of same length.
  *
  * For instance, assume that aa and bb are buses with 3 bits.
  * When aa evaluates to 100 (4 in decimal) and bb to 110 (6 in decimal),
  * then the gate buildUnsignedLess(aa, bb) evaluates to true.
  *
  * Hints: 
  *
  * (1)  You should preferably solve the assignment "buildEqual" 
  *      before attempting this assignment. 
  * (2)  Do not worry, you need not engage in serious thinking yet,
  *      this is why we have hints ... :-)
  * (3)  All right, so precisely what is it that makes one 
  *      (unsigned) binary integer ___less than___ another? 
  *      Let us put our thinking cap on and engage in some (guided)
  *      thinking. 
  *      To start with, in most cases when one is forced to think, 
  *      it is a good strategy to think first in terms of concrete 
  *      examples. Above we used 100 (4 in decimal) and 110 (6 in decimal) 
  *      as an example. So what is it that makes 100 less than 110 ? 
  *      Well, obviously the sequences need to differ in at least
  *      one bit position, since otherwise the sequences would be
  *      equal. Indeed, we observe that 100 and 110 differ in position 1.
  *      Furthermore, 100 is __less than__ 110 __in position 1__. 
  *      Aha! It looks like we are getting somewhere. 
  * (4)  So now let us think really hard (again with guidance to ease
  *      the amount of thinking that needs to be done), 
  *      in terms of bus1 and bus2. Exactly when is it the case
  *      that bus1 is less than bus2 ? 
  *      If bus1 and bus2 do not differ in any position, they are equal. 
  *      So let us assume that bus1 and bus2 are different, in one or 
  *      more position(s). Exactly when is it the case that bus1 is less 
  *      than bus2 ? 
  * (5)  Suppose j is the __largest__ position in which bus1 and bus2
  *      differ (remember that we now assume that there is at least one
  *      such j, so we may just as well consider the largest such j). 
  * (6)  We claim that bus1 is __less than__ bus2 if and only if it holds 
  *      that both  bus1(j) has the value 0  (here 0 is the same as false) 
  *            and  bus2(j) has the value 1  (here 1 is the same as true).  
  *      [[Indeed, you may want to formally prove this for yourself,
  *      say, by observing that 2^j is greater than any binary integer that
  *      has nonzero bits in only the j-1 least significant bit positions.
  *      Recall the power series summation formula. For the purposes of
  *      this assignment however we need no such proof.]]
  * (7)  Now we understand exactly when bus1 is less than bus2,
  *      so all that remains to be done is to translate this understanding 
  *      into a circuit design. 
  * (8)  So let us think really hard what we want the circuit to do.
  *      Let us use the intuition above to define a goal that we want
  *      achieve with our design.
  * (9)  Suppose our goal is to have a gate g(i) whose value is true 
  *      if and only if the largest value j <= i, if there is such a value, 
  *      where bus1(j) and bus2(j) are different, is a value where 
  *      bus1(j) has the value 0 and bus2(j) has the value 1. 
  *      (Or in slightly less precise terms, the 
  *      largest difference up to position i, if any, indicates
  *      that bus1 is less than bus2.)
  * (10) Our goal looks a bit tedious, but it has been chosen with
  *      care to meet our objective. Indeed, if we have
  *      gate g(l-1) with l == bus1.length == bus2.length, we are done:
  *      then the value of gate g(l-1) tells us exactly whether bus1 
  *      is less than bus2.
  * (11) At the other extreme, we observe that gate g(0) is easy to construct.
  *      Indeed, g(0) is true if and only if bus1(0) is false 
  *      and bus2(0) is true. 
  * (12) Here comes the final, hard part as regards thinking. 
  *      Suppose we have available gate g(i-1), and we must 
  *      construct gate g(i). What should we do? 
  *      Well, of course we have available also the values bus1(i)
  *      and bus2(i). In fact, we can formulate a truth table
  *      for g(i) based on the values of bus1(i), bus2(i), and g(i-1).
  *      We display the table below, with comments to justify 
  *      each value of g(i).
  *
  *      bus1(i)  bus2(i)  g(i-1) | g(i) | Comments
  *      -------------------------+------+-----------------------------------
  *         0       0         0   |   0  | no difference at i, so forward i-1
  *         0       0         1   |   1  | no difference at i, so forward i-1
  *         0       1         0   |   1  | difference at i, less than
  *         0       1         1   |   1  | difference at i, less than
  *         1       0         0   |   0  | difference at i, not less than
  *         1       0         1   |   0  | difference at i, not less than
  *         1       1         0   |   0  | no difference at i, so forward i-1
  *         1       1         1   |   1  | no difference at i, so forward i-1
  * 
  * (13) Now you can synthesize the circuit, using, say, one for-loop 
  *      over i, and perhaps using in the loop a helper function of 
  *      the form ...
  *
  *      def buildGategi(b1i: Gate, b2i: Gate, giminus1: Gate): Gate = 
  *        ???
  *     
  *
  *      ... that returns a "gate g(i)" when given a "gate bus1(i)",
  *      a "gate bus2(i)", and a "gate g(i-1)" as parameters. 
  *      Look at function "buildAdder" below for an example on how to
  *      make a for-loop over the gates of two buses. 
  * (14) Recall how we synthesized half adders and full adders
  *      from a truth table in the Round 2 material. The same procedure
  *      can be applied to the truth table in (12). 
  * (15) If all else fails, build the circuit first for a bus of
  *      length 1, then for a bus of length 2, then for a bus of length 3,
  *      and so forth, in each case using Toggler to check that your
  *      circuit works. 
  *
  */
  def buildUnsignedLess(bus1: Bus, bus2: Bus): Gate =
    require(
      bus1.length == bus2.length,
      "Can only compare buses of the same length"
    )
    require(bus1.length >= 1, "Cannot do unsigned comparison of empty buses")
    def buildGategi(b1i: Gate, b2i: Gate, giminus1: Gate): Gate =
      val b1iLess    = (!b1i) && b2i
      val b1iEqual   = (!(b1i || b2i)) || (b1i && b2i)
      (b1iLess) || (b1iEqual && giminus1)

    (bus1 zip bus2).foldLeft(Gate.False)((giminus, b) => buildGategi(b._1, b._2, giminus))
  end buildUnsignedLess

  /**
  * The unsigned less-than-or-equal comparator.
  * Returns a gate that evaluates to true if and only if
  * the evaluated value of bus1 is less than or equal to that of bus2
  * in unsigned binary.
  * The buses must be non-empty and of same length.
  *
  * For instance, assume that aa and bb are buses with 3 bits.
  * When aa evaluates to 100 (4 in decimal) and bb to 110 (6 in decimal),
  * then the gate buildUnsignedLessOrEqual(aa, bb) evaluates to true.
  *
  * Hint: 
  * Here comes the payoff from hard work above :-) 
  * You can call the builder methods that you have already implemented 
  * above. One line of code is enough for this task. 
  *
  */ 
  def buildUnsignedLessOrEqual(bus1: Bus, bus2: Bus): Gate =
    buildUnsignedLess(bus1, bus2) || buildEqual(bus1, bus2)

  end buildUnsignedLessOrEqual

  /** A helper method implementing a half adder.
  * @return
  *   a pair (c_out, s), where
  *   - c_out is a gate evaluating to true iff both argument gates are true,
  *     and
  *   - s is a gate evaluating to true iff an odd number of inputs are true.
  */
  protected def buildHalfAdder(a: Gate, c_in: Gate): (Gate, Gate) =
    val c_out = a && c_in
    val s = (a || c_in) && !c_out
    (c_out, s)
  end buildHalfAdder

  /**
  * A helper method implementing a full adder.
  * @return a pair (c_out, s), where
  * (i)  c_out is a gate evaluating to true iff at least two of the argument 
  *      gates are true, and
  * (ii) s is a gate evaluating to true iff an odd number of inputs are true.
  */
  protected def buildFullAdder(a: Gate, b: Gate, c_in: Gate): (Gate, Gate) =
    val c_out =
      (!a && b && c_in) || (a && !b && c_in) || (a && b && !c_in) || (a && b && c_in)
    val s =
      (!a && !b && c_in) || (!a && b && !c_in) || (a && !b && !c_in) || (a && b && c_in)
    (c_out, s)
  end buildFullAdder

  /**
  * Unsigned incrementer with modulo semantics.
  * Returns a new bus of the same length whose evaluated value 
  * (in unsigned binary) is the value of the argument bus incremented 
  * with 1 (modulo 2^bus.length).
  *
  * For instance, assume that aa is a bus with 3 bits.
  * When aa evaluates to 011 (3 in decimal), then the bus 
  * buildIncrementer(aa) should evaluate to 100 (4 in decimal).
  * Similarly, when aa evaluates to 111 (7 in decimal), then 
  * buildIncrementer(aa) should evaluate to 000 (8 mod 8 = 0 in decimal).
  *
  */
  def buildIncrementer(bus: Bus): Bus =
    require(bus.length > 0, "Cannot build incrementer of an empty bus")
    val bits = new Array[Gate](bus.length)
    bits(0) = Gate.True
    for i <- 1 until bus.length do
      bits(i) = Gate.False
    val busOne = new Bus(bits.toIndexedSeq)

    buildAdder(bus, busOne)
  end buildIncrementer

  /**
  * A simple ripple carry adder with modulo semantics.
  * Returns a bus that evaluates to the sum of values of the argument buses
  * (modulo 2^bus1.length).
  * The argument buses must be of the same length.
  * The resulting bus is of the same length, as well.
  */
  def buildAdder(bus1: Bus, bus2: Bus) =
    require(bus1.length == bus2.length, "Can only add buses of same length")
    var carry_in: Gate = Gate.False // no initial carry
    val ss = new Array[Gate](bus1.length)
    for i <- 0 until bus1.length do
      val (carry_out, sum) = buildFullAdder(bus1(i), bus2(i), carry_in)
      carry_in = carry_out // carry from bit i propagates to bit i+1
      ss(i) = sum
    new Bus(ss.toIndexedSeq)
  end buildAdder

  /**
  * A simple ripple carry adder with no overflow.
  * Returns a bus that evaluates to the sum of values of the argument buses.
  * The resulting bus is of the length (bus1.length max bus2.length)+1.
  *
  * For instance, assume that aa and bb are buses with 3 bits.
  * When aa evaluates to 011 (3 in decimal) and bb to 110 (6 in decimal),
  * then the bus buildAdderNoOverflow(aa, bb) should evaluate to 1001 
  * (9 in decimal).
  *
  */
  def buildAdderNoOverflow(bus1: Bus, bus2: Bus) =
    require(bus1.length > 0, "Can not build an adder on an empty bus")
    require(bus2.length > 0, "Can not build an adder on empty bus")
    val newBusLength = (bus1.length max bus2.length) + 1
    var carry_in: Gate = Gate.False // no initial carry
    val ss = new Array[Gate](newBusLength)
    for i <- 0 until newBusLength - 1 do
      val g1 = if i < bus1.length then bus1(i) else Gate.False
      val g2 = if i < bus2.length then bus2(i) else Gate.False
      val (carry_out, sum) = buildFullAdder(g1, g2, carry_in)
      carry_in = carry_out // carry from bit i propagates to bit i+1
      ss(i) = sum
    ss(newBusLength - 1) = carry_in
    new Bus(ss.toIndexedSeq)
  end buildAdderNoOverflow

  /** A helper for computing base 2 logarithms */
  protected def log2ceil(i: Int) =
    var v = 0
    var t = i
    while t != 0 do
      v += 1
      t = t >> 1
    v
  end log2ceil

  /**
  * Count number of true bits in a bus.
  * Returns a bus that evaluates (in binary) to the sum of true values in 
  * the argument bus.
  * The resulting bus is of the length ceil(log2(bus.length)) 
  * [you can use the log2ceil function defined above].
  *
  * For instance, assume that aa is a bus with 3 bits. Now the bus 
  * buildCountTrueBits(aa) is a bus with 2 bits.
  * When aa evaluates to 101, then the bus buildCountTrueBits(aa) 
  * should evaluate to 10 (2 in decimal).
  *
  * As another example, assume that aa is a bus with 4 bits. 
  * Now the bus buildCountTrueBits(aa) is a bus with 3 bits.
  * When aa evaluates to 1111, then the bus buildCountTrueBits(aa) 
  * should evaluate to 100 (4 in decimal).
  *
  * Hint:
  * Perhaps you can use some of the builder functions above?
  *
  */
  def buildCountTrueBits(bus: Bus): Bus =
    require(bus.length > 0, "Can not build a true bit counter on an empty bus")
    val resultLength = log2ceil(bus.length)
    var countingBus = new Bus(IndexedSeq.fill(resultLength)(Gate.False))
    for g <- bus do
      val adderBits = new Array[Gate](resultLength)
      adderBits(0) = g
      for i <- 1 until resultLength do
        adderBits(i) = Gate.False

      val adderBus = new Bus(adderBits.toIndexedSeq)
      countingBus = buildAdder(countingBus, adderBus)

    countingBus
  end buildCountTrueBits

  /**
  * Majority tester.
  * Returns a gate that evaluates to true if more than half of the bits 
  * in the input bus evaluate to true.
  *
  * For instance, assume that aa is a bus with 3 bits.
  * When aa evaluates to 101, then the gate buildMajorityTester(aa) 
  * should evaluate to true.
  *
  * As another example, assume that aa is a bus with 4 bits.
  * When aa evaluates to 1001, then the gate buildMajorityTester(aa) 
  * should evaluate to false.
  *
  * Hint:
  * Again we have done most of the hard work above, now it suffices 
  * to resort to fruits of hard labour.
  *
  */ 
  def buildMajorityTester(bus: Bus): Gate =
    require(bus.length > 0, "Can not build a majority tester on an empty bus")
    val halfLength = bus.length / 2
    val countedBus = buildCountTrueBits(bus)
    val bits = new Array[Gate](countedBus.length)

    for i <- 0 until countedBus.length do
      if ((halfLength >> i) & 1) == 1 then
        bits(i) = Gate.True
      else
        bits(i) = Gate.False

    val halfBus = new Bus(bits.toIndexedSeq)
    buildUnsignedLess(halfBus, countedBus)


  end buildMajorityTester

  /**
  * Unsigned multiplier circuit builder.
  * multiplier(aa, bb) returns a bus cc that evaluates to the product of 
  * values of the buses aa and bb.
  * The resulting bus cc is of length aa.length+bb.length.
  *
  * For instance, if aa is a bus with 3 bits and bb is a bus with 4 bits,
  * then cc = buildMultiplier(aa, bb) is a bus with 7 bits.
  * When aa evaluates to the unsigned binary number 011 (3 in decimal) and
  * bb to 1100 (12 in decimal), then cc should evaluate to 0100100 
  *  (36 in decimal).
  *
  * Hints:
  * (1)  You may use the "primary school multiplication" algorithm
  * (2)  "buildAdder" or "buildAdderNoOverflow" may be a useful sub-routine
  * (3)  As class Bus extends scala.collection.Seq, you can use its methods
  *      as well (e.g. ++ to concatenate two buses).
  */
  def buildMultiplier(bus1: Bus, bus2: Bus): Bus =
    require(bus1.length > 0, "Cannot multiply with an empty bus")
    require(bus2.length > 0, "Cannot multiply with an empty bus")
    val resultLength = bus1.length + bus2.length
    var result = new Bus(IndexedSeq.fill(resultLength)(Gate.False))
    for i <- 0 until bus2.length do
      val productBits = for j <- 0 until bus1.length yield bus1(j) && bus2(i)
      val productBus = new Bus(productBits)
      val shiftedBus = new Bus (
        IndexedSeq.fill(i)(Gate.False) ++
        productBus ++
        IndexedSeq.fill(resultLength - i - bus1.length)(Gate.False)
      )
      result = buildAdder(result, shiftedBus)

    result


  end buildMultiplier

end factory
