
package tinylog
import scala.collection.{SpecificIterableFactory, StrictOptimizedSeqOps, mutable}
import collection.SeqOps

sealed class Bus(gates: Seq[Gate]) extends Seq[Gate]
    with SeqOps[Gate, Seq, Bus]
    with StrictOptimizedSeqOps[Gate, Seq, Bus]:

  // Mandatory implementation of `apply` in SeqOps
  def apply(idx: Int) = gates.apply(idx)

  /** Creates a new Bus from a set of indexes to this one.*/
  def apply(idxs: Seq[Int]) = new Bus(idxs.map(gates(_)))

  // Mandatory implementation of `length` and `iterator`
  def length = gates.length
  def iterator = gates.iterator

  /* Operations on Gates.*/

  /** Values of Gates.*/
  def values = gates.map(_.value)

  /** The number of gates (i) in this bus and (ii) recursively referenced by the ones in this bus. */
  def nofGates: Int =
    val counted = new mutable.HashSet[Gate]()
    gates.foldLeft(0)((result, gate) => result + gate.nofReferenced(counted))

  /**
   * For a bus aa and gate g, aa && g returns a new bus cc
   * of length aa.length such that cc(i) is aa(i) && g.
   */
  def &&(that: Gate) = new Bus(this.map(_ && that))

  /**
   * For a bus aa and gate g, aa || g returns a new bus cc
   * of length aa.length such that cc(i) is aa(i) || g.
   */
  def ||(that: Gate) = new Bus(this.map(_ || that))

  /**
   * Bitwise negation of the bus.
   * For a bus aa, ~aa is a new bus cc such that cc(i) is !aa(i).
   */
  def unary_~ = this.map(!_)

  /**
   * Bitwise AND of two busses.
   * For two busses aa and bb, aa & bb returns a new bus cc
   * of length aa.length such that cc(i) is aa(i) && bb(i).
   * The busses must be of the same length.
   */
  def &(that: Bus) =
    require(this.length == that.length, "Cannot take bitwise and of busses of different length")
    new Bus((this zip that).map(x => x._1 && x._2))

  /**
   * Bitwise OR of two busses.
   * For two busses aa and bb, aa | bb returns a new bus cc
   * of length aa.length such that cc(i) is aa(i) || bb(i).
   * The busses must be of the same length.
   */
  def |(that: Bus) =
    require(this.length == that.length, "Cannot take bitwise and of busses of different length")
    new Bus((this zip that).map(x => x._1 || x._2))
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


object Bus extends SpecificIterableFactory[Gate, Bus]:

  def empty: Bus = new Bus(Seq.empty)

  def newBuilder: mutable.Builder[Gate,Bus] =
    mutable.ArrayBuffer.newBuilder[Gate]
    .mapResult(s=>new Bus(s.toSeq))

  def fromSpecific(it: IterableOnce[Gate]): Bus = it match
    case seq: Seq[Gate] => new Bus(seq)
    case _ => new Bus(it.iterator.toSeq)

  /** Returns a new bus with n InputElement gates */
  def inputs(n: Int) = new Bus((1 to n).map(x => Gate.input()))
  /** Returns a new bus of n False gates */
  def falses(n: Int) = new Bus((1 to n).map(x => Gate.False))
  /** Returns a new bus of n True gates */
  def trues(n: Int) = new Bus((1 to n).map(x => Gate.True))


