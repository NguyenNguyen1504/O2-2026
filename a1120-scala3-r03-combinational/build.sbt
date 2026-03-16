

ThisBuild / scalaVersion := "3.3.7"

lazy val commonSettings = Seq(
    scalaVersion := "3.3.7",
    Test / parallelExecution := false,
)


lazy val gates = (project in  file("a03-gates"))
.settings(commonSettings,testDeps)
.dependsOn(tinylog)
lazy val circuits = (project in  file("a04-circuits"))
.settings(commonSettings,testDeps)
.dependsOn(tinylog)
lazy val busWrite = (project in  file("a05-busWrite"))
.settings(commonSettings,testDeps)
.dependsOn(tinylog)
lazy val shallowOps = (project in  file("a06-shallowOps"))
.settings(commonSettings,testDeps)
.dependsOn(tinylog)

lazy val tinylog = (project in file("tinylog"))
  .settings(commonSettings, guiDeps,
  )


lazy val guiDeps = Seq(
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0"),
)


lazy val testDeps = Seq(
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
)
