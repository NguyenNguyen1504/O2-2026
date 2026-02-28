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
  * A simple class representing pixels, constructed using the Int representation
  * of RGBA color value. This means that different channels are packed into the Int
  * as (hi bis) RRRRRRRRGGGGGGGGBBBBBBBBAAAAAAAA (low bits), where R = red channel,
  * G = green channel, B = blue channel and A = alpha channel. Color value is immutable.
  * 
  * The reason why we would want to pack all of the color components into a single Int
  * is that the values for each color channel, at least in the usual RGB cases range from
  * 0 to 255. This means that we only need 8 bits per component, and therefore storing
  * only one color component in an Int would waste 24 bits. However, as 4*8 = 32, we
  * can store EXACTLY 4 such components in the storage space associated with a single
  * Int if we pack them properly. This also happens the number of channels in RGBA,
  * where RGB are the familiar Red, Green and Blue channels, and A is the alpha channel
  * representing the transparency of that pixel. For more information see
  * https://en.wikipedia.org/wiki/RGBA_color_model
  *
  * @param rgbaInt Int representation of the color, using RGBA
  */ 
class Pixel(rgbaInt: Int):

  /** Get the raw RGBA Int value
    * @return Int representation of the color, using RGBA
    */
  def v: Int = this.rgbaInt

  /** Get the different color channels separated into a tuple
    * @return A tuple where the channels are (R, G, B, A)
    */
  def rgba: (Int, Int, Int, Int) =
    ???
  end rgba

  /**
    * Allows equality comparisons
    */
  override def equals(x: Any): Boolean =
    x match
      case other: Pixel => other.v == this.v
      case _            => false
  end equals

  /**
    * For printing convenience
    */
  override def toString(): String =
    val rgbaHex = this.rgbaInt.toHexString
                    .reverse
                    .padTo(8, '0')
                    .reverse
                    .toCharArray()
                    .grouped(2)
                    .map(_.mkString)
                    .mkString(" ")
    var s = s"\nRGBA = 0x ${rgbaHex}\n"
    try
      s = s + s"R = ${rgba._1}, G = ${rgba._2}, B = ${rgba._3}, A = ${rgba._4}\n"
    catch
      case e: NotImplementedError =>
        s = s + "... component separation not yet implemented in pixel.scala ...\n"
    
    return s
  end toString

end Pixel


/**
  * Companion object that has some convenience methods for creating the Pixel instances
  */
object Pixel:
  // Use raw int
  def apply(rgbaInt: Int): Pixel = new Pixel(rgbaInt)

  // Use rgba values separately
  def apply(r: Int, g: Int, b: Int, a: Int): Pixel =
    new Pixel(((r & 0xFF) << 24) | ((g & 0xFF) << 16) | ((b & 0xFF) << 8) | (a & 0xFF))
  end apply

end Pixel