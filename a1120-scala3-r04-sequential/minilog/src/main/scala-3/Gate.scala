package minilog

/** The base class for gates.
  *
  * Build gates from existing gates with Boolean operators or
  * manufacture via factory methods in class Circuit.
  *
  */

sealed abstract class Gate(val host: Circuit):
  host.registerGate(this)       // register this gate with the host

  var depth = 0

  /** Convenience constructor for use by extending classes. */
  protected def this(inputs: Gate*) =
    this(inputs.head.host)      // host is the host of first input
    require(inputs.tail.forall(_.host == inputs.head.host))
      // fails unless all inputs have same host
    depth = 1 + inputs.map(_.depth).max
  end this

  /* Memoization and clean/dirty interface 
   * (internal to classes Gate and Circuit). */

  /** The memorized value of this gate. */
  private var memo = false              // defaults to false

  /** Updates memorized value, invoked by host. */
  private[minilog] def clean() = { memo = eval }

  /** Returns the value of this gate, implemented in extending classes. */
  protected def eval: Boolean

  /** Returns the (memorized) value of this gate. */
  def value = 
    if host.dirty then
      host.clean()       // recompute all memos if dirty
    memo                 // my memorized value is up to date, so return it

  /** Sets the value of this input element (fails on other gate types). */
  def set(v: Boolean) = { require(false) }         // fails unless input element

  /** Builds a feedback to this input element from another element `g`.
    Feedback can be built from any type of gate `g`, but only to an input element.
    Therefore, the base implementation in the abstract class Gate of this method always fails.
    See the overloaded method in case class InputElement for actual implementation.
    */
  def buildFeedbackFrom(g: Gate) =
    require(false,"buildFeedbackFrom can only be called on InputElement gates. It is not possible to build feedback to non InputElement.")
  // fails unless input element

  /* Builders for basic types of gates. */

  /** Returns a new NOT-gate. */
  def unary_!     = new NotGate(this)

  /** Returns a new AND-gate. */
  def &&(that: Gate): Gate = new AndGate(this, that)

  /** Returns a new OR-gate. */
  def ||(that: Gate): Gate = new OrGate(this, that)

  /** Returns a new XOR-gate. */
  def +(that: Gate): Gate  = new XorGate(this, that)

  /** Returns a new XOR-gate. */
  def ^^(that: Gate): Gate = new XorGate(this, that)

end Gate

/** Implements an input element. */
sealed class InputElement(host: Circuit) extends Gate(host):
  host.registerInput(this)          // register the new input

  /** Value of this input element. */
  private var v = false                     // default value is false

  /** Sets the value of this input element. */
  override def set(s: Boolean) = 
    v = s                           // whenever an input is set ...
    host.dirty = true               // ... flag the host dirty 
  end set

  /** Returns the value of this input element. */
  protected def eval = v

  /** The gate from which this input element takes feedback. */
  private var feedback_from: Gate = this    // default feedback is from itself

  /** Builds a feedback to this input element. */
  override def buildFeedbackFrom(g: Gate) = 
    require(host == g.host, "Feeback can only be built between gates with the same host.")         // fail unless g has the same host
    feedback_from = g 
  end buildFeedbackFrom

  /** Returns the value of the feedback gate. */
  def feedbackValue = feedback_from.value
end InputElement

/** Implements a NOT gate. */
sealed class NotGate(in: Gate) extends Gate(in):
  protected def eval = !in.value
end NotGate

/** Implements an OR gate. */
sealed class OrGate(in1: Gate, in2: Gate) extends Gate(in1, in2):
  protected def eval = in1.value || in2.value
end OrGate

/** Implements an AND gate. */
sealed class AndGate(in1: Gate, in2: Gate) extends Gate(in1, in2):
  protected def eval = in1.value && in2.value
end AndGate

/** Implements a XOR gate. */
sealed class XorGate(in1: Gate, in2: Gate) extends Gate(in1, in2):
  protected def eval = (in1.value || in2.value) && !(in1.value && in2.value)
end XorGate

/** Implements a constant gate. */
sealed class ConstantGate(host: Circuit, v: Boolean) extends Gate(host):
  protected def eval = v
end ConstantGate
