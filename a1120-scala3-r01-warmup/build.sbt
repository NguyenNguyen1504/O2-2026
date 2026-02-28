

ThisBuild / scalaVersion := "3.3.7"

lazy val commonSettings = Seq(
    scalaVersion := "3.3.7",
    Test / parallelExecution := false,
)


lazy val sequences = (project in  file("a01-sequences"))
.settings(commonSettings,testDeps)
lazy val courseGrading = (project in  file("a02-courseGrading"))
.settings(commonSettings,testDeps)
lazy val collatz = (project in  file("a04-collatz"))
.settings(commonSettings,testDeps)
lazy val polynomials = (project in  file("a05-polynomials"))
.settings(commonSettings,testDeps)
lazy val longestIncreasingSubsequence = (project in  file("a06-longestIncreasingSubsequence"))
.settings(commonSettings,testDeps)

lazy val testDeps = Seq(
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
)
