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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class QoiSpec extends AnyFlatSpec with Matchers:

  // Helper functions and definitions to create small test inputs
  val endPadding: IndexedSeq[Byte] = IndexedSeq.fill(7)(0.toByte) :+ 1.toByte
  def mkRGB(r: Int, g: Int, b: Int): IndexedSeq[Byte] =
    IndexedSeq[Byte](0xFE.toByte, r.toByte, g.toByte, b.toByte)
  def mkRGBA(r: Int, g: Int, b: Int, a: Int): IndexedSeq[Byte] =
    IndexedSeq[Byte](0xFF.toByte, r.toByte, g.toByte, b.toByte, a.toByte)
  def mkRun(l: Int): IndexedSeq[Byte] =
    assert(l > 0 && l < 63, "Run length must be in [1, 62]")
    IndexedSeq[Byte]((0xC0 | (l - 1)).toByte)
  def mkDiff(dR: Int, dG: Int, dB: Int): IndexedSeq[Byte] =
    assert(dR >= -2 && dR < 2 && dG >= -2 && dG < 2 && dB >= -2 && dB < 2, "Deltas must be in [-2, 1]")
    IndexedSeq[Byte]((0x40 | ((dR + 2) << 4) | ((dG + 2) << 2) | (dB + 2)).toByte)
  def mkLuma(dR: Int, dG: Int, dB: Int): IndexedSeq[Byte] =
    assert(dG >= -32 && dG < 32 && (dR - dG) >= -8 && (dR - dG) < 8 && (dB - dG) >= -8 && (dB - dG) < 8,
      "Unsuitable deltas. dG must be in [-32, 32), and dR and dB must be [-8, 8) FROM THAT")
    IndexedSeq[Byte]((0x80 | (dG + 32)).toByte, (((dR - dG + 8) << 4) | (dB - dG + 8)).toByte)
  def mkInd(i: Int): IndexedSeq[Byte] =
    assert(i >= 0 && i < 64, "Index must be in [0, 64)")
    IndexedSeq[Byte](i.toByte)

  // Tests

  "Pixel" should "return the destructured (R, G, B, A) tuple correctly" in {
    val rand = scala.util.Random(347)
    val n = 20
    for _ <- 0 until n do
      val r = rand.nextInt(256)
      val g = rand.nextInt(256)
      val b = rand.nextInt(256)
      val a = rand.nextInt(256)
      val pix = Pixel(r, g, b, a)
      pix.rgba shouldBe (r, g, b, a)
    end for
  }

  "QOI decoder" should "return an empty IndexedSeq if given just the end padding" in {
    qoiDecode(endPadding) shouldBe IndexedSeq[Int]()
  }

  val rgbaCases = Seq(
      (200, 0, 0, 255),
      (0, 200, 0, 255),
      (0, 0, 200, 255),
      (159, 5, 20, 128),
      (102, 228, 119, 110),
      (151, 5, 20, 136),
      (167, 158, 4, 116),
      (1, 38, 236, 129)
    )

  it should "work correctly when encountering an encoding of raw RGB values" in {
    for (r, g, b, _) <- rgbaCases do
      val reference = Pixel(r, g, b, 255)
      val decoded = qoiDecode(mkRGB(r, g, b) ++ endPadding)
      withClue("Should've decoded only 1 pixel") {
        decoded should have length (1)
      }
      val decPix = new Pixel(decoded.head)
      withClue("Decoded pixel") {
        decPix shouldBe reference
      }
    end for
  }

  it should "work correctly when encountering an encoding of raw RGBA values" in {
    for (r, g, b, a) <- rgbaCases do
      val reference = Pixel(r, g, b, a)
      val decoded = qoiDecode(mkRGBA(r, g, b, a) ++ endPadding)
      withClue("Should've decoded only 1 pixel") {
        decoded should have length (1)
      }
      val decPix = new Pixel(decoded.head)
      withClue("Decoded pixel") {
        decPix shouldBe reference
      }
    end for
  }

  it should "work correctly when encountering an encoding of repeated pixels" in {
    val rand = scala.util.Random(347)
    // use the rgba cases as the start
    for (r, g, b, a) <- rgbaCases do
      val runLen = rand.nextInt(32) + 1
      val reference = Pixel(r, g, b, a)
      val decoded = qoiDecode(mkRGBA(r, g, b, a) ++ mkRun(runLen) ++ endPadding)
      withClue(s"Should've decoded 1 base pixel and then $runLen additional\n") {
        decoded should have length (runLen + 1)
      }
      val decPixs = decoded.map(i => new Pixel(i))
      all(decPixs) shouldBe reference
    end for
  }

  it should "work correctly when encountering a single-byte-encoded diff" in {
    val rand = scala.util.Random(347)
    val n = 5
    for _ <- 0 until n do
      val len = 50
      val diffsRGB = IndexedSeq.fill(len)(
        (rand.nextInt(4) - 2, rand.nextInt(4) - 2, rand.nextInt(4) - 2)
      )
      val encoding = diffsRGB.flatMap((dR, dG, dB) => mkDiff(dR, dG, dB)) ++ endPadding
      val decoded = qoiDecode(encoding)
      withClue(s"Should've decoded $len pixels\n") {
        decoded should have length (len)
      }
      val referenceRGBs = diffsRGB.scanLeft((0, 0, 0))(
        (prev, deltas) => (prev._1 + deltas._1, prev._2 + deltas._2, prev._3 + deltas._3)
      ).tail
       .map((r, g, b) => (r & 0xFF, g & 0xFF, b & 0xFF))  // Ensure that the values are in the range [0, 256)
      val decPixs = decoded.map(i => new Pixel(i))
      val refPixs = referenceRGBs.map((r, g, b) => Pixel(r, g, b, 255))
      for ((dec, ref), ind) <- decPixs.zip(refPixs).zipWithIndex do
        withClue(s"At index $ind decoded pixel") {
          dec shouldBe ref
        }
    end for
  }

  it should "work correctly when encountering a dual-byte-encoded diff" in {
    val rand = scala.util.Random(347)
    // larger diffs (luma)
    val m = 5
    for _ <- 0 until m do
      val len = 50
      val largeDiffsRGB = IndexedSeq.fill(len)({
        val dG = rand.nextInt(64) - 32
        val dR = dG + rand.nextInt(16) - 8
        val dB = dG + rand.nextInt(16) - 8
        (dR, dG, dB)
      })
      val encoding = largeDiffsRGB.flatMap((dR, dG, dB) => mkLuma(dR, dG, dB)) ++ endPadding
      val decoded = qoiDecode(encoding)
      withClue(s"Should've decoded $len pixels\n") {
        decoded should have length (len)
      }
      val referenceRGBs = largeDiffsRGB.scanLeft((0, 0, 0))(
        (prev, deltas) => (prev._1 + deltas._1, prev._2 + deltas._2, prev._3 + deltas._3)
      ).tail
       .map((r, g, b) => (r & 0xFF, g & 0xFF, b & 0xFF))  // Ensure that the values are in the range [0, 256)
      val decPixs = decoded.map(i => new Pixel(i))
      val refPixs = referenceRGBs.map((r, g, b) => Pixel(r, g, b, 255))
      for ((dec, ref), ind) <- decPixs.zip(refPixs).zipWithIndex do
        withClue(s"At index $ind decoded pixel") {
          dec shouldBe ref
        }
    end for
  }

  it should "work correctly when encountering an index encoding" in {
    import scala.collection.mutable.Buffer
    // Use the rgba cases as the basis
    val (hR, hG, hB, hA) = rgbaCases.head
    val encoding = Buffer[Byte]() ++= mkRGBA(hR, hG, hB, hA)
    val refRGBA = Buffer[(Int, Int, Int, Int)](rgbaCases.head)
    for (cur, prev) <- rgbaCases.tail.zip(rgbaCases) do
      encoding ++= mkRGBA(cur._1, cur._2, cur._3, cur._4)
      refRGBA += cur
      encoding ++= mkInd(qoiIndexHash(prev._1, prev._2, prev._3, prev._4))
      refRGBA += prev
    end for
    val decoded = qoiDecode(encoding.toIndexedSeq ++ endPadding)
    withClue(s"Should've decoded ${refRGBA.length} pixels\n") {
      decoded should have length (refRGBA.length)
    }
    val decPixs = decoded.map(i => new Pixel(i))
    val refPixs = refRGBA.map((r, g, b, a) => Pixel(r, g, b, a))
    for ((dec, ref), ind) <- decPixs.zip(refPixs).zipWithIndex do
      withClue(s"At index $ind decoded pixel") {
        dec shouldBe ref
      }
    end for
  }

  it should "properly decode actual images" in {
    val imgPath = "a07-qoi/data/"
    val imgBaseNames = IndexedSeq(
      "checkerboard",
      "ultra_mango",
      "white_noise",
      "floating_orb",
      "Aalto_logo",
      //"city_LARGE",
      //"shore_LARGE"
    )
    for imgName <- imgBaseNames do
      val refImg: BufferedImage = ImageIO.read(new File(imgPath + imgName + ".png"))
      val refPixels = refImg.getRGB(0, 0, refImg.getWidth(), refImg.getHeight(),
                                    null, 0, refImg.getWidth())
      // Map ref pixels from ARGB to RGBA
      refPixels.mapInPlace( argbInt => ((argbInt << 8) & 0xFFFFFF00) | ((argbInt >> 24) & 0x000000FF) )
      readQoiFile(imgPath + imgName + ".qoi", qoiDecode) match
        case Some((decodedRGBAs, width, height)) =>
          withClue("Should have decoded the correct number of pixels") {
            decodedRGBAs should have length (refPixels.length)
          }
          for ((decPix, refPix), ind) <- decodedRGBAs.zip(refPixels).zipWithIndex do
            /* Not the proper scalatest way, but creating the clue (and therefore
               the two pixel objects first for every tested pixel pair is too costly) */
            if decPix != refPix then
              fail(s"In image \"$imgName\" the pixel at y = ${ind / width} and x = ${ind % width} " +
                   s"was incorrect\n${new Pixel(decPix)} wasn't ${new Pixel(refPix)}")
          end for
        case None =>
          fail(s"Could not decode qoi image $imgName.qoi!")
    end for
  }


end QoiSpec
