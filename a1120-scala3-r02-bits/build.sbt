

ThisBuild / scalaVersion := "3.3.7"

lazy val commonSettings = Seq(
    scalaVersion := "3.3.7",
    Test / parallelExecution := false,
)


lazy val wordOps = (project in  file("a04-wordOps"))
.settings(commonSettings,testDeps)
lazy val parity = (project in  file("a05-parity"))
.settings(commonSettings,testDeps)
lazy val base64 = (project in  file("a06-base64"))
.settings(commonSettings,testDeps)
lazy val qoi = (project in  file("a07-qoi"))
.settings(commonSettings,testDeps,guiDeps)
lazy val rationalDecompose = (project in  file("a08-rationalDecompose"))
.settings(commonSettings,testDeps)

lazy val testDeps = Seq(
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
)


lazy val guiDeps = Seq(
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0"),
)
