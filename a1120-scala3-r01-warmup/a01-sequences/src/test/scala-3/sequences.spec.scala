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


package sequences

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class sequenceSpec extends AnyFlatSpec with Matchers:
    "addOneToSequence" should "add 1 to all elements of the sequence" in {
        addOneToSeq(Seq[Int]()) should equal (Seq[Int]())
        addOneToSeq(Seq(-47)) should equal (Seq(-46))
        addOneToSeq(Seq(-37)) should equal (Seq(-36))
        addOneToSeq(Seq(-77, 8, 70)) should equal (Seq(-76, 9, 71))
        addOneToSeq(Seq(69, 60, -97)) should equal (Seq(70, 61, -96))
        addOneToSeq(Seq(50, 25, -9, 24, 40)) should equal (Seq(51, 26, -8, 25, 41))
        addOneToSeq(Seq(-90, -19, -62, 35, 45)) should equal (Seq(-89, -18, -61, 36, 46))
    }

    "removeOdd" should "remove all odd numbers from the sequence" in {
        removeOdd(Seq[Int]()) should equal (Seq[Int]())
        removeOdd(Seq(46)) should equal (Seq(46))
        removeOdd(Seq(51)) should equal (Seq[Int]())
        removeOdd(Seq(98, 48, 44)) should equal (Seq(98, 48, 44))
        removeOdd(Seq(65, 13, 3)) should equal (Seq[Int]())
        removeOdd(Seq(90, 6, 11)) should equal (Seq(90, 6))
        removeOdd(Seq(63, 5, 22, 95, 19)) should equal (Seq(22))
        removeOdd(Seq(65, 48, 41, 93, 94)) should equal (Seq(48, 94))
        withClue("When there are negative integers:") {
            removeOdd(Seq(-3, 0, 5, 12)) should equal (Seq(0, 12))
        }
    }

    "mySum" should "sum all of the elements in the sequences" in {
        mySum(Seq(0)) shouldBe 0
        mySum(Seq(36)) shouldBe 36
        mySum(Seq(48, 44, 65)) shouldBe 157
        mySum(Seq(62, 56, 95)) shouldBe 213
        mySum(Seq(5, 22, 95, 19, 65)) shouldBe 206
        mySum(Seq(33, 10, 6, 92, 20)) shouldBe 161
    }

    "sumOfSquaresOfOdd" should "behave as described" in {
        sumOfSquaresOfOdd(Seq(0, 3)) shouldBe 9
        sumOfSquaresOfOdd(Seq(44, 63)) shouldBe 3969
        sumOfSquaresOfOdd(Seq(95, 23, 89, 90)) shouldBe 17475
        sumOfSquaresOfOdd(Seq(6, 22, 97, 19)) shouldBe 9770
        sumOfSquaresOfOdd(Seq(97, 98, 33, 20, 77, 43, 68, 47)) shouldBe 20485
        sumOfSquaresOfOdd(Seq(84, 93, 26, 56, 63, 99, 40, 65)) shouldBe 26644
        withClue("When there are negative integers:"){
            sumOfSquaresOfOdd(Seq(-6, -3, 2, 7)) shouldBe 58
        }
    }

    "productOfNonZero" should "multiply all non zero elements in the sequence" in {
        productOfNonzero(Seq(2, 0)) shouldBe 2
        productOfNonzero(Seq(0, -1)) shouldBe -1
        productOfNonzero(Seq(0, -5, 1, 0)) shouldBe -5
        productOfNonzero(Seq(3, 0, 3, 2)) shouldBe 18
        productOfNonzero(Seq(2, -1, -3, -5, 2, 0, 0, 3)) shouldBe -180
        productOfNonzero(Seq(-5, 0, 2, -1, 3, 1, -3, -2)) shouldBe 180
        productOfNonzero(Seq(3, 3, 3, -2, 3, -4, -5, 0)) shouldBe -3240
        productOfNonzero(Seq(1, 2, 2, 1, -1, -5, 2, 0)) shouldBe 40
    }

    "minOfSquaresAtLeast17OfPositive" should "behave as described" in {
        minOfSquaresAtLeast17OfPositive(Seq(100, -28)) shouldBe 10000
        minOfSquaresAtLeast17OfPositive(Seq(30, -12)) shouldBe 900
        minOfSquaresAtLeast17OfPositive(Seq(40, -6, 2, 18, 5)) shouldBe 25
        minOfSquaresAtLeast17OfPositive(Seq(-20, 1, 35, 90)) shouldBe 1225
        minOfSquaresAtLeast17OfPositive(Seq(-88, -78, 82, -90)) shouldBe 6724
        minOfSquaresAtLeast17OfPositive(Seq(32, -48, 20, 86, 48, -42, 64, -18)) shouldBe 400
        minOfSquaresAtLeast17OfPositive(Seq(-40, -54, -50, 35, 58, 26, 46, -92)) shouldBe 676
    }

    "sequenceOfSecondPartsWhoseFirstPartIsEven" should "behave as described" in {
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq[(Int, Int)]()) should equal (Seq[Int]())
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((46, 36))) should equal (Seq(36))
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((51, 98))) should equal (Seq[Int]())
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((65, 3), (13, 44))) should equal (Seq[Int]())
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((62, 95), (56, 21))) should equal (Seq(95, 21))
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((63, 19), (5, 65), (22, 48), (95, 41))) should equal (Seq(48))
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((93, 58), (94, 62), (14, 20), (12, 32))) should equal (Seq(62, 20, 32))
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((60, 59), (93, 62), (84, 90), (29, 18), (82, 23), (41, 31), (8, 60), (22, 23))) should equal (Seq(59, 90, 23, 60, 23))
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((73, 99), (87, 40), (98, 63), (84, 44), (93, 92), (26, 66), (56, 30), (63, 23))) should equal (Seq(63, 44, 66, 30))
        sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((84, 38), (11, 90), (94, 81), (58, 24), (90, 23), (84, 11), (97, 3), (2, 96))) should equal (Seq(38, 81, 24, 23, 11, 96))
        withClue("When there are negative integers:") {
            sequenceOfSecondPartsWhoseFirstPartIsEven(Seq((-4, 5), (-1, 15))) should equal (Seq[Int](5))
        }
    }

    "sumOfProductsOfPairs" should "behave as described" in {
        sumOfProductsOfPairs(Seq((46, 36))) shouldBe 1656
        sumOfProductsOfPairs(Seq((51, 48), (98, 44))) shouldBe 6760
        sumOfProductsOfPairs(Seq((65, 3), (13, 44))) shouldBe 767
        sumOfProductsOfPairs(Seq((89, 63), (90, 5), (6, 22), (11, 95))) shouldBe 7234
        sumOfProductsOfPairs(Seq((19, 93), (65, 94), (48, 14), (41, 12))) shouldBe 9041
        sumOfProductsOfPairs(Seq((46, 82), (78, 41), (66, 8), (26, 22), (60, 59), (93, 62), (84, 90), (29, 18))) shouldBe 25458
        sumOfProductsOfPairs(Seq((23, 93), (31, 26), (60, 56), (23, 63), (73, 99), (87, 40), (98, 63), (84, 44))) shouldBe 28331
    }

    "sumOfTwoDDimVectors" should "behave as described" in {
        sumOfTwoDDimVectors(Seq[Int](), Seq[Int]()) should equal (Seq[Int]())
        sumOfTwoDDimVectors(Seq(-47), Seq(-12)) should equal (Seq(-59))
        sumOfTwoDDimVectors(Seq(-37), Seq(-77)) should equal (Seq(-114))
        sumOfTwoDDimVectors(Seq(69, 60, -97), Seq(-43, -19, -13)) should equal (Seq(26, 41, -110))
        sumOfTwoDDimVectors(Seq(-60, 13, 83), Seq(93, 26, -97)) should equal (Seq(33, 39, -14))
        sumOfTwoDDimVectors(Seq(-52, -80, -90, -19, -62), Seq(35, 45, 56, -98, -66)) should equal (Seq(-17, -35, -34, -117, -128))
        sumOfTwoDDimVectors(Seq(28, 79, 69, 85, -2), Seq(19, -45, 15, 25, 23)) should equal (Seq(47, 34, 84, 110, 21))
    }

    "innerProductOfTwoDDimVectors" should "behave as described" in {
        innerProductOfTwoDDimVectors(Seq(-2), Seq(3)) shouldBe -6
        innerProductOfTwoDDimVectors(Seq(-37), Seq(-77)) shouldBe 2849
        innerProductOfTwoDDimVectors(Seq(-97, -43, -19), Seq(-13, -60, 13)) shouldBe 3594
        innerProductOfTwoDDimVectors(Seq(83, 93, 26), Seq(-97, 50, 25)) shouldBe -2751
        innerProductOfTwoDDimVectors(Seq(-90, -19, -62, 35, 45), Seq(56, -98, -66, 28, 79)) shouldBe 5449
        innerProductOfTwoDDimVectors(Seq(-80, -45, 31, -34, 59), Seq(96, -28, 26, 74, -93)) shouldBe -13617
    }

    "squareOfDDimEuclideanDistance" should "behave as described" in {
        squareOfDDimEuclideanDistance(Seq(-2), Seq(3)) shouldBe 25
        squareOfDDimEuclideanDistance(Seq(-37), Seq(-77)) shouldBe 1600
        squareOfDDimEuclideanDistance(Seq(-97, -43, -19), Seq(-13, -60, 13)) shouldBe 8369
        squareOfDDimEuclideanDistance(Seq(83, 93, 26), Seq(-97, 50, 25)) shouldBe 34250
        squareOfDDimEuclideanDistance(Seq(-90, -19, -62, 35, 45), Seq(56, -98, -66, 28, 79)) shouldBe 28778
        squareOfDDimEuclideanDistance(Seq(69, 85, -2, 19, -45), Seq(15, 25, 23, 83, 34)) shouldBe 17478
    }

    "pairWithOffset" should "behave as described" in {
        pairWithOffset(Seq(-2), Seq(3), 0) should equal (Seq((-2, 3)))
        pairWithOffset(Seq(-77), Seq(8), 0) should equal (Seq((-77, 8)))
        pairWithOffset(Seq(-60, 13, 83), Seq(93, 26, -97), 1) should equal (Seq((-60, 26), (13, -97)))
        pairWithOffset(Seq(-45, -100, -14), Seq(-10, 68, -54), 2) should equal (Seq((-45, -54)))
        pairWithOffset(Seq(79, 69, 85, -2, 19), Seq(-45, 15, 25, 23, 83), 1) should equal (Seq((79, 15), (69, 25), (85, 23), (-2, 83)))
        pairWithOffset(Seq(-80, -45, 31, -34, 59), Seq(96, -28, 26, 74, -93), 0) should equal (Seq((-80, 96), (-45, -28), (31, 26), (-34, 74), (59, -93)))
    }

    "consecutivePositionPairs" should "behave as described" in {
        consecutivePositionPairs(Seq(-2)) should equal (Seq[(Int, Int)]())
        consecutivePositionPairs(Seq(-12, -37, -77)) should equal (Seq((-12, -37), (-37, -77)))
        consecutivePositionPairs(Seq(8, 70, 69)) should equal (Seq((8, 70), (70, 69)))
        consecutivePositionPairs(Seq(26, -97, 50, 25, -9)) should equal (Seq((26, -97), (-97, 50), (50, 25), (25, -9)))
        consecutivePositionPairs(Seq(24, 40, 72, 59, -23)) should equal (Seq((24, 40), (40, 72), (72, 59), (59, -23)))
        consecutivePositionPairs(Seq(35, 45, 56, -98, -66, 28, 79)) should equal (Seq((35, 45), (45, 56), (56, -98), (-98, -66), (-66, 28), (28, 79)))
        consecutivePositionPairs(Seq(69, 85, -2, 19, -45, 15, 25)) should equal (Seq((69, 85), (85, -2), (-2, 19), (19, -45), (-45, 15), (15, 25)))
    }

    "firstMaxPos" should "behave as described" in {
        firstMaxPos(Seq(-47)) shouldBe 0
        firstMaxPos(Seq(-12, -37)) shouldBe 0
        firstMaxPos(Seq(-77, 8)) shouldBe 1
        firstMaxPos(Seq(5, 5)) shouldBe 0
        firstMaxPos(Seq(13, 27, -95, 27)) shouldBe 1
        firstMaxPos(Seq(-13, -60, 13, 83)) shouldBe 3
        firstMaxPos(Seq(-100, -14, -10, 68, -54, -63, 5, 92)) shouldBe 7
        firstMaxPos(Seq(-45, 31, -34, 59, 96, -28, 26, 74)) shouldBe 4
    }

    "sumAndDifferenceSeqs" should "behave as described" in {
        sumAndDifferenceSeqs(Seq(-47, -12), Seq(-37, -77)) should equal ((Seq(-84, -89), Seq(-10, 65)))
        sumAndDifferenceSeqs(Seq(8, 70), Seq(69, 60)) should equal ((Seq(77, 130), Seq(-61, 10)))
        sumAndDifferenceSeqs(Seq(-60, 13, 83), Seq(93, 26, -97)) should equal ((Seq(33, 39, -14), Seq(-153, -13, 180)))
        sumAndDifferenceSeqs(Seq(50, 25, -9), Seq(24, 40, 72)) should equal ((Seq(74, 65, 63), Seq(26, -15, -81)))
        sumAndDifferenceSeqs(Seq(45, 56, -98, -66, 28), Seq(79, 69, 85, -2, 19)) should equal ((Seq(124, 125, -13, -68, 47), Seq(-34, -13, -183, -64, 9)))
        sumAndDifferenceSeqs(Seq(-45, 15, 25, 23, 83), Seq(34, -80, -45, 31, -34)) should equal ((Seq(-11, -65, -20, 54, 49), Seq(-79, 95, 70, -8, 117)))
        sumAndDifferenceSeqs(Seq(59, 96, -28, 26, 74), Seq(-93, -36, 98, 43, 74)) should equal ((Seq(-34, 60, 70, 69, 148), Seq(152, 132, -126, -17, 0)))
    }

    "stringsConcatenated" should "behave as described" in {
        stringsConcatenated(Seq[String]()) shouldBe ""
        stringsConcatenated(Seq("")) shouldBe ""
        stringsConcatenated(Seq("wy", "y")) shouldBe "wyy"
        stringsConcatenated(Seq("ww", "x")) shouldBe "wwx"
        stringsConcatenated(Seq("yy", "")) shouldBe "yy"
        stringsConcatenated(Seq("", "xw", "yx", "")) shouldBe "xwyx"
        stringsConcatenated(Seq("zzy", "w", "zw", "")) shouldBe "zzywzw"
        stringsConcatenated(Seq("yzw", "yzz", "", "", "y", "y", "wxy", "yzx")) shouldBe "yzwyzzyywxyyzx"
        stringsConcatenated(Seq("zxx", "ww", "", "", "zxw", "", "", "z")) shouldBe "zxxwwzxwz"
        stringsConcatenated(Seq("xw", "", "yx", "z", "yy", "xyw", "xzy", "yyz")) shouldBe "xwyxzyyxywxzyyyz"
        stringsConcatenated(Seq("xwx", "yx", "yy", "y", "zz", "xy", "", "yzw")) shouldBe "xwxyxyyyzzxyyzw"
    }