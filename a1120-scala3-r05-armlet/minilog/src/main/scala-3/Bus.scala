package minilog

import scala.collection.{SpecificIterableFactory, StrictOptimizedSeqOps, mutable}
import collection.SeqOps

/** A custom collection for bus-level building. */
sealed class Bus(gates: Seq[Gate]) extends Seq[Gate]
    with SeqOps[Gate, Seq, Bus]
    with StrictOptimizedSeqOps[Gate, Seq, Bus]:

  /* Relegate to underlying sequence object. */
  def host = gates.head.host
  def length = gates.length
  def apply(idx: Int) = gates.apply(idx)
  def apply(idxs: Seq[Int]) = new Bus(idxs.map(gates(_)))
  def iterator = gates.iterator

  /** Returns the values of the gates in the bus. */
  def values = gates.map(_.value)

  /** Returns the gate-wise AND of the gates in the left operand with the right operand. */
  def &&(that: Gate) = new Bus(this.map(_ && that))

  /** Returns the gate-wise OR of the gates in the left operand with the right operand. */
  def ||(that: Gate) = new Bus(this.map(_ || that))

  /** Returns the gate-wise XOR of the gates in the left operand with the right operand. */
  def +(that: Gate)  = new Bus(this.map(_ + that))

  /** Returns the NOT of all gates in the operand. */
  def unary_~        = this.map(!_)

  /** Returns the gate-wise AND of its operands. */
  def &(that: Bus)   = new Bus((this zip that).map(x => x._1 && x._2))

  /** Returns the gate-wise OR of its operands. */
  def |(that: Bus)   = new Bus((this zip that).map(x => x._1 || x._2))

  /** Returns the gate-wise XOR of its operands. */
  def ^(that: Bus)   = new Bus((this zip that).map(x => x._1 + x._2))

  /** Builds feedbacks to each gate (input element) in the bus from the corresponding 
    * gate in the operand. */
  def buildFeedbackFrom(that: Bus) : Unit =
    require(this.length == that.length,"Can only build feedback between buses of same length.")
    (this zip that).foreach(x => x._1.buildFeedbackFrom(x._2))
  end buildFeedbackFrom

  /* Because Bus is a custom collection (based on Seq) with SeqOps trait
     we need to override a a few methods so that it can inherit all of the
     standard operations from the trait while still behaving as a Bus as
     much as possible.
     If you are interested, see the RNA exampe at 
     https://docs.scala-lang.org/overviews/core/custom-collections.html#final-version-of-rna-strands-class
   */

  // Mandatory overrides of `fromSpecific`, `newSpecificBuilder`,
  // and `empty`, from `IterableOps`
  override protected def fromSpecific(coll: IterableOnce[Gate]): Bus =
    Bus.fromSpecific(coll)

  override protected def newSpecificBuilder: mutable.Builder[Gate, Bus] =
    Bus.newBuilder

  override def empty: Bus = Bus.empty

  // Overloading of `appended`, `prepended`, `appendedAll`, `prependedAll`,
  // `map`, `flatMap` and `concat` to return a `Bus` when possible
  def concat(suffix: IterableOnce[Gate]): Bus =
    strictOptimizedConcat(suffix, newSpecificBuilder)

  @inline final def ++ (suffix: IterableOnce[Gate]): Bus = concat(suffix)

  def appended(base: Gate): Bus =
    (newSpecificBuilder ++= this += base).result()

  def appendedAll(suffix: Iterable[Gate]): Bus =
    strictOptimizedConcat(suffix, newSpecificBuilder)

  def prepended(base: Gate): Bus =
    (newSpecificBuilder += base ++= this).result()

  def prependedAll(prefix: Iterable[Gate]): Bus =
    (newSpecificBuilder ++= prefix ++= this).result()

  def map(f: Gate => Gate): Bus =
    strictOptimizedMap(newSpecificBuilder, f)

  def flatMap(f: Gate => IterableOnce[Gate]): Bus =
    strictOptimizedFlatMap(newSpecificBuilder, f)

  // The class name will by default be shown as 'Seq', we don't want that.
  override def className = "Bus"

end Bus

/** A companion builder for class Bus. */
object Bus extends SpecificIterableFactory[Gate, Bus]:
  
  def empty: Bus = new Bus(Seq.empty)

  def newBuilder: mutable.Builder[Gate,Bus] =
    mutable.ArrayBuffer.newBuilder[Gate]
    .mapResult(s=>new Bus(s.toSeq))

  def fromSpecific(it: IterableOnce[Gate]): Bus = it match
    case seq: Seq[Gate] => new Bus(seq)
    case _ => new Bus(it.iterator.toSeq)

end Bus
