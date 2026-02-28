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


package base64

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class base64Spec extends AnyFlatSpec with Matchers:
    "to24Bits" should "returns the correct 24-bit word" in {
        val test = Seq(((0xFF, 0x00, 0x00, "0xFF, 0x00, 0x00"), 0xFF0000),
        ((0x00, 0xFF, 0x00, "0x00, 0xFF, 0x00"), 0x00FF00),
        ((0x00, 0x00, 0xFF, "0x00, 0x00, 0xFF"), 0x0000FF),
        ((0x11, 0x22, 0x33, "0x11, 0x22, 0x33"), 0x112233),
        ((0xFF, 0xFF, 0xFF, "0xFF, 0xFF, 0xFF"), 0xFFFFFF), 
        ((0xffffff11, 0xffffff88, 0xffffff00, "0xffffff11, 0xffffff88, 0xffffff00"), 0x118800))

        for ((input1, input2, input3, format), answer) <- test do
            withClue("For input %s, ".format(format)) {
                to24Bits(input1, input2, input3) shouldBe answer 
            }
    }

    "to6BitWords" should "returns the correct array" in {
        val test = Seq(((0x00003F, "0x00003F"), IndexedSeq(0, 0, 0, 0x3F)),
        ((0x000FC0, "0x000FC0"), IndexedSeq(0, 0, 0x3F, 0)), 
        ((0x03F000, "0x03F000"), IndexedSeq(0, 0x3F, 0, 0)),
        ((0xFC0000, "0xFC0000"), IndexedSeq(0x3F, 0, 0, 0)),
        ((0xFFFFFF, "0xFFFFFF"), IndexedSeq(63, 63, 63, 63)), 
        ((0xFEFEFE, "0xFEFEFE"), IndexedSeq(63, 47, 59, 62)))

        for ((input, format), answer) <- test do
            withClue("For input %s, ".format(format)) {
                to6BitWords(input) should equal (answer)
            }
    }

    "restrictedEncode" should "returns the correct encoded word" in {
        val test = Seq(((IndexedSeq[Byte](), "IndexedSeq()"), ""), 
        ((IndexedSeq[Byte](77, 97, 110), "IndexedSeq(77, 97, 110)"), "TWFu"), 
        ((IndexedSeq[Byte](-1, -1, -1), "IndexedSeq(-1, -1, -1)"), "////"), 
        ((IndexedSeq[Byte](77, 97, 110, -1, -1, -1), "IndexedSeq(77, 97, 110, -1, -1, -1)"), "TWFu////"),
        ((IndexedSeq[Byte](-2, -2, -2), "IndexedSeq(-2, -2, -2)"), "/v7+"),
        ((IndexedSeq[Byte](-19, 31, -124, -32, 1, -71, -115, 43, 31, 61, -116, 9, 89, -34, 6, -127, -80, 79, 47, 79, 35, 118, -115, -81, -24, 95, -37), 
            "IndexedSeq(-19, 31, -124, -32, 1, -71, -115, 43, 31, 61, -116, 9, 89, -34, 6, -127, -80, 79, 47, 79, 35, 118, -115, -81, -24, 95, -37)"), 
            "7R+E4AG5jSsfPYwJWd4GgbBPL08jdo2v6F/b"))
        
        for ((input, format), answer) <- test do
            withClue("For input %s, ".format(format)) {
                restrictedEncode(input) should equal (answer)
            }
    }

    "encode" should "returns the correct encoded word" in {
        val test = Seq(((IndexedSeq[Byte](), "IndexedSeq()"), ""), 
        ((IndexedSeq[Byte](-1), "IndexedSeq(-1)"), "/w=="), 
        ((IndexedSeq[Byte](-1, 1), "IndexedSeq(-1, 1)"), "/wE="), 
        ((IndexedSeq[Byte](1, 2, 3), "IndexedSeq(1, 2, 3)"), "AQID"),
        ((IndexedSeq[Byte](1, 2, 3, 4), "(1, 2, 3, 4)"), "AQIDBA=="),
        (("any carnal pleasure.".getBytes("UTF-8").toIndexedSeq, 
            "\"any carnal pleasure.\".getBytes(\"UTF-8\").toIndexedSeq"), 
            "YW55IGNhcm5hbCBwbGVhc3VyZS4="), 
        (("any carnal pleasure".getBytes("UTF-8").toIndexedSeq, 
            "\"any carnal pleasure\".getBytes(\"UTF-8\").toIndexedSeq"), 
            "YW55IGNhcm5hbCBwbGVhc3VyZQ=="),
        (("any carnal pleasur".getBytes("UTF-8").toIndexedSeq, 
            "\"any carnal pleasur\".getBytes(\"UTF-8\").toIndexedSeq"), 
            "YW55IGNhcm5hbCBwbGVhc3Vy"), 
        (("Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.".getBytes("UTF-8").toIndexedSeq, 
        "\"Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.\".getBytes(\"UTF-8\").toIndexedSeq"),
        "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4="))
    
        for ((input, format), answer) <- test do
            withClue("For input %s\n".format(format)) {
                encode(input) should equal (answer)
            }
    }