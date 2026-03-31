package armlet

import collection.immutable.Queue


import minilog.*
/** A circuit with support for feedback hooks.**/
class HookedCircuit() extends Circuit:
  /* Interface for feedback hooks. */
  /* Only needed for Armlet simulations. */
  /** Feedback hooks register here. */ 
  private var hooks = Queue[() => (() => Unit)]()

  /** Builds a feedback hook. */
  def buildFeedbackHook(hook: () =>  (() => Unit)) = { hooks = hooks :+ hook } 

  /** Executes feedback with hooks */
  override def clock() =
    val writehooks = hooks.map(_()) // run read hooks
    (ins zip ins.map(_.feedbackValue)).foreach((w,v) => w.set(v))
    writehooks.foreach(_())         // run write hooks
  end clock


end HookedCircuit
