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
/**
 * Returns the Programming 2 grade when provided information
 * on the number of points the student received in the exercises,
 * the exam, and possible bonus points from feedback.
 *
 * For more information on assessment, see the MyCourses page.
 *
 * Note:
 * - examPart2Percentage is from 0.0 to 100.0 and
 * - validSubmittedFeedbackRounds is the number of
 *   rounds where the student submitted feedback and received
 *   points from at least one exercise.
 */


def calculateGrade(
  pointsA: Int,
  pointsB: Int,
  pointsC: Int,
  examPart1Points: Int,
  examPart2Percentage: Double,
  validSubmittedFeedbackRounds: Int,
  myCoursesFeedbackSubmitted: Boolean,
): Int =

  require(pointsA >= 0)
  require(pointsB >= 0)
  require(pointsC >= 0)
  require(examPart1Points >= 0)
  require(validSubmittedFeedbackRounds >= 0)
  require(0.0 <= examPart2Percentage && examPart2Percentage <= 100.0)

  val feedbackPoints = if myCoursesFeedbackSubmitted then 50 else 0
  val totalPointsA = validSubmittedFeedbackRounds * 5 + feedbackPoints
  val exercisePoint = pointsA + pointsB + pointsC + totalPointsA

  if examPart1Points < 5 then 0
  else if exercisePoint >= 3500 && pointsB >= 1300 && examPart2Percentage >= 80 then 5
  else if exercisePoint >= 3000 && pointsB >= 1000 && examPart2Percentage >= 65 then 4
  else if exercisePoint >= 2400 && pointsB >= 800  && examPart2Percentage >= 50 then 3
  else if exercisePoint >= 1800 && pointsB >= 600                               then 2
  else if exercisePoint >= 1200 && pointsB >= 200                               then 1
  else 0


end calculateGrade
