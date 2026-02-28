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
/*
 * This file contains functions for reading the binary .qoi files.
 * You don't have to modify these.
 */

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.awt.Image
import java.awt.image.BufferedImage

/**
  * @param path The path needed to read the image file
  * @return Some(IndexedSeq of RGBA Ints, width, height) when ANY pixel data could be decoded,
  *         None if no decoded data could be retrieved
  */
def readQoiFile(path: String,
                decoderFunc: IndexedSeq[Byte] => IndexedSeq[Int]): Option[(IndexedSeq[Int], Int, Int)] =
  var file: FileInputStream = null
  try
    file = new FileInputStream(path)
  catch
    case e: FileNotFoundException =>
      println(s"Error: Did not find or could not open file $path")
      return None
  
  val contents = file.readAllBytes()
  file.close()
  
  if contents.length < 14 then
    // A problem has occured, didn't load even the header
    println(s"Read file $path had a length of ${contents.length} bytes, which is too little")
    return None

  val (header, data) = contents.splitAt(14)

  val qoiMagic = Array[Byte]('q'.toByte, 'o'.toByte, 'i'.toByte, 'f'.toByte)
  if !qoiMagic.corresponds(header.take(4))(_ == _) then
    val ms = header.take(4).map(b => uByteToInt(b).toChar).mkString(" ")
    println(s"The magic number at the start of file was $ms, was supposed to be qoif")
    return None

  val width = header.slice(4, 8)
                    .map(uByteToInt)
                    .reduceLeft((l, r) => (l << 8) | r)
  val height = header.slice(8, 12)
                    .map(uByteToInt)
                    .reduceLeft((l, r) => (l << 8) | r)
  // don't care about channels and colorspace now
  
  var decoded = IndexedSeq[Int]()

  try
    decoded = decoderFunc(data.toIndexedSeq)
  catch
    case e: Exception =>
      println(s"Error: While decoding caught exception - ${e.getMessage()}")
      return None
  
  return Some((decoded, width, height))

end readQoiFile

/**
  * @param path The path needed to read the image file
  * @return Some(BufferedImage) when successfull, None when failed
  */
def readQoiFileToImage(path: String): Option[BufferedImage] =
  readQoiFile(path, qoiDecode).map({ (pixels, width, height) =>
    // Check if pixels has the proper number of pixels
    val hRoundUp = (pixels.length + width - 1) / width
    if hRoundUp * width != pixels.length then
      println(s"Decoded ${pixels.length} pixels which is wrong for $width * $height image\n" +
              "Returning an image padded to the next full row by transparent pixels")
    val img = new BufferedImage(width, hRoundUp, BufferedImage.TYPE_INT_ARGB)
    // map from RGBA representation to ARGB which is used by java images
    val argbs = pixels.map( rgbaInt => ((rgbaInt >>> 8) & 0x00FFFFFF) | ((rgbaInt << 24) & 0xFF000000) )
    img.setRGB(0, 0, width, hRoundUp, argbs.padTo(width * hRoundUp, 0).toArray, 0, width)
    img
  })
end readQoiFileToImage