name := "Pokemon"

version := "0.1"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "16.0.0-R25",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.5",
)

// sbt assembly
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
