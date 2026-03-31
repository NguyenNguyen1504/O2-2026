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





package memory:

  import minilog._

  /*
  * Run this object to play with memory units in Trigger.
  */
  @main def main(): Unit =

    /** Create new Circuit and Trigger instances. */
    val circuit = new Circuit()
    val trigger = new Trigger(circuit)
    
    /** Address and data word lengths in bits. */
    val addressLength = 4
    val dataLength = 16

    /** Create the parameters for the memory units. The address parameter is not 
        needed for the one-word memory unit. */
    val readEnable  = circuit.input()
    val writeEnable = circuit.input()
    val address = circuit.inputs(addressLength)
    val data = circuit.inputs(dataLength)
     
     /** Set up Trigger. */
    trigger.watch("readEnable", readEnable)
    trigger.watch("writeEnable", writeEnable)
    trigger.watch("address", address.reverse)   // Used only in a multiple-word memory
    trigger.watch("data", data.reverse)
   
    // REMEMBER TO UNCOMMENT ONE OF THESE
    // TO CONSTRUCT A readOutput
    /** Build the one-word memory unit. */
    // val readOutput = factory.buildOneWordMemory(readEnable, writeEnable, data)
    /** Build the multiple-word memory unit. */
    // val readOutput = factory.buildMemory(readEnable, writeEnable, address, data)

    // AND THIS TO SEE YOUR OUTPUT IN trigger
    // trigger.watch("readOutput", readOutput.reverse)    
    
    /** Go Trigger. */
    trigger.go()
  end main


