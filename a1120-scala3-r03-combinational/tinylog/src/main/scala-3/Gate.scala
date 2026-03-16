package tinylog

/** A class for "timestamps" */
class TimeStamp {
}

/** The abstract base class for all our Boolean gate types */
sealed abstract class Gate() 
:
  def unary_! = new NotGate(this)
  def &&(that: Gate): Gate = new AndGate(this, that)
  def ||(that: Gate): Gate = new OrGate(this, that)


  protected var memoValue: Boolean = false
  protected var memoTimeStamp: TimeStamp = null
  def value: Boolean =
    if memoTimeStamp == Gate.updatedTimeStamp then memoValue
    else
      memoValue = _eval
      memoTimeStamp = Gate.updatedTimeStamp
      memoValue
  protected def _eval: Boolean
  def depth(implicit counted: scala.collection.mutable.Map[Gate, Int] = new scala.collection.mutable.HashMap[Gate, Int]()): Int
  /**
   * The number of gates recursively referenced by this gate (including the gate itself) that are
   * not already in the set "counted".
   * The set "counted" is updated while evaluating the result.
   */
  def nofReferenced(implicit counted: scala.collection.mutable.Set[Gate] = new scala.collection.mutable.HashSet[Gate]()): Int

/**
 * Companion object allowing easier construction of constant and input gates
 * */
object Gate:
  /** A "time stamp",
   * updated to indicate that an input gate has changed value
   * */
  var updatedTimeStamp = new TimeStamp()

  val False: Gate = new ConstantGate(false)
  val True: Gate  = new ConstantGate(true)
  def input() = new InputElement()

sealed class InputElement() extends Gate()
:
  var v = false                     // default value is false
  def set(s: Boolean) = {v = s; Gate.updatedTimeStamp = new TimeStamp() }
  def _eval = v
  def depth(implicit counted: scala.collection.mutable.Map[Gate, Int]) = 0
  def nofReferenced(implicit counted: scala.collection.mutable.Set[Gate]) = if counted.add(this) then 1 else 0

sealed class NotGate(in: Gate) extends Gate()                
:
  def _eval = !in.value
  def depth(implicit counted: scala.collection.mutable.Map[Gate, Int]) = counted.getOrElseUpdate(this, in.depth(counted)+1)
  def nofReferenced(implicit counted: scala.collection.mutable.Set[Gate]) = if counted.add(this) then in.nofReferenced(counted) + 1 else 0

sealed class OrGate(in1: Gate, in2: Gate) extends Gate()
:
  def _eval = in1.value || in2.value
  def depth(implicit counted: scala.collection.mutable.Map[Gate, Int]) = counted.getOrElseUpdate(this, (in1.depth max in2.depth)+1)
  def nofReferenced(implicit counted: scala.collection.mutable.Set[Gate]) =
    if counted.add(this) then in1.nofReferenced(counted) + in2.nofReferenced(counted) + 1 else 0

sealed class AndGate(in1: Gate, in2: Gate) extends Gate()
:
  def _eval = in1.value && in2.value
  def depth(implicit counted: scala.collection.mutable.Map[Gate, Int]) = counted.getOrElseUpdate(this, (in1.depth max in2.depth)+1)
  def nofReferenced(implicit counted: scala.collection.mutable.Set[Gate]) =
    if counted.add(this) then in1.nofReferenced(counted) + in2.nofReferenced(counted) + 1 else 0

sealed class ConstantGate(v: Boolean) extends Gate()
:
  def _eval = v
  def depth(implicit counted: scala.collection.mutable.Map[Gate, Int]) = 0
  def nofReferenced(implicit counted: scala.collection.mutable.Set[Gate]) = if counted.add(this) then 1 else 0
