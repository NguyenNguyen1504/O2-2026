

ThisBuild / scalaVersion := "3.3.7"

lazy val commonSettings = Seq(
    scalaVersion := "3.3.7",
    Test / parallelExecution := false,
)


lazy val oscillator = (project in  file("a02-oscillator"))
.settings(commonSettings,testDeps)
.dependsOn(minilog)
lazy val lfsr = (project in  file("a03-lfsr"))
.settings(commonSettings,testDeps)
.dependsOn(minilog)
lazy val memory = (project in  file("a04-memory"))
.settings(commonSettings,testDeps)
.dependsOn(minilog)
lazy val seqmul = (project in  file("a05-seqmul"))
.settings(commonSettings,testDeps)
.dependsOn(minilog)
lazy val pipelining = (project in  file("a06-pipelining"))
.settings(commonSettings,testDeps)
.dependsOn(minilog)

lazy val armlet = (project in file("armlet"))
  .settings(commonSettings, guiDeps,
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  )
  .dependsOn(minilog)


lazy val guiDeps = Seq(
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0"),
)


lazy val testDeps = Seq(
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
)


lazy val minilog = (project in file("minilog"))
  .settings(commonSettings, guiDeps,
  )
