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


package courseGrading

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class GradingSpec extends AnyFlatSpec with Matchers:

    "calculateGrade" should "calculate the grade correctly for a few known inputs" in {
        val known = Seq(
            //A     B     C     P1  P2     FR   MyCo 
            (( 975,  200,    0,  5,   0.0,  5, false ), 1),
            ((1200,  600,   10,  5,   0.0,  0,  true ), 2),
            ((1600,  800,   50,  7,  50.0,  0, false ), 3),
            ((1800, 1000,  150,  7,  65.0,  5,  true ), 4),
            (( 200, 1300, 2000,  9,  93.0,  0,  true ), 5),
            ((1800, 1000,  150,  4,   0.0,  5,  true ), 0),
        )

        for ((pointsA, pointsB, pointsC, examPart1Points,
              examPart2Percentage, validSubmittedFeedbackRounds,
              myCoursesFeedbackSubmitted), grade) <- known do
            withClue(
                s"""For inputs pointsA=$pointsA, pointsB=$pointsB, pointsC=$pointsC,
                   |  examPart1Points=$examPart1Points, examPart2Percentage=$examPart2Percentage,
                   |  validSubmittedFeedbackRounds=$validSubmittedFeedbackRounds,
                   |  myCoursesFeedbackSubmitted=$myCoursesFeedbackSubmitted,
                   |your function output""".stripMargin) {
                calculateGrade(pointsA, pointsB, pointsC, examPart1Points, examPart2Percentage, validSubmittedFeedbackRounds, myCoursesFeedbackSubmitted) shouldBe grade
            }
    }

end GradingSpec
