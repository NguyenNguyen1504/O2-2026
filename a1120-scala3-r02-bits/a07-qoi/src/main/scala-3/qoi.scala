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


package qoi

/** 
  * If you want to convert a Byte to an Int, be carefull, since Bytes
  * in scala are SIGNED and in range [-128, 127], but we want to interpret
  * them as unsigned, so having the range [0, 255]. This function is a helper
  * to convert a Byte to an Int safely, such that we get a value in the
  * desired range.
  * 
  * The conversion to the other direction is safe directly with .toByte
  *  
  * @param b A regular Byte which we want to read as UNSIGNED
  * @return An Int with value in [0, 255]
  */
def uByteToInt(b: Byte): Int = b.toInt & 0xFF

/**
  * The hash function used in the qoi specification, needed whenever
  * we must find the index in the Array of previously seen pixels.
  * 
  * For the purposes of this exercise you don't need to know very much
  * about hash functions. In short, they are functions that take inputs
  * from a very large input space (in this case all the possible RGBA
  * pixels) and map them into outputs in some smaller output space (in
  * this case integers in [0, 63])
  * 
  * @param r  Red color component, will zero all other bits except 8 lowest
  * @param g  Green color component, will zero all other bits except 8 lowest
  * @param b  Blue color component, will zero all other bits except 8 lowest
  * @param a  Alpha component, will zero all other bits except 8 lowest
  * @return Int in range [0, 63]
  */
def qoiIndexHash(r: Int, g: Int, b: Int, a: Int): Int =
  ((r & 0xFF) * 3 + (g & 0xFF) * 5 + (b & 0xFF) * 7 + (a & 0xFF) * 11) % 64
    

/**
  * Decodes the qoi-encoded input bytes and returns the image
  * pixels packed to Ints in RGBA format. Notice that we
  * don't have to care about whether the original image had 3
  * or 4 channels (so whether it had a dedicated alpha channel).
  * We can just set the alpha to 255 unless the encoding specifically
  * states otherwise. Also notice that this function does not have to
  * worry about the file header mentioned in the specification, `bytes`
  * is just the encoded pixel data ++ end padding.
  * 
  * If the input consist of only the end padding, the length of which
  * is 8 Bytes, the output should be an empty IndexedSeq.
  * 
  * @param bytes an IndexedSeq of qoi encoded bytes
  * @return An IndexedSeq of pixel color values packed to Ints in RGBA format
  */
def qoiDecode(bytes: IndexedSeq[Byte]): IndexedSeq[Int] =

  // Here are some data structures needed for the decoding

  // The QOI specification refers to an array of previously seen pixels, use this for it
  val prevSeenPix = Array.fill(64)(Pixel(0))
  // Put every decoded pixel into this Buffer, we will return it at the end of the function
  val result = scala.collection.mutable.ArrayBuffer[Int]()

  ???
  // Now we are done, the pixels should be in result.
  // Convert to an indexed sequence and return.
  result.toIndexedSeq
end qoiDecode
