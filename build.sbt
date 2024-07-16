name := "Pokemon"

version := "0.1"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "16.0.0-R25",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.5",
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
