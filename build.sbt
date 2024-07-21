name := "Pokemon"

version := "0.1"

scalaVersion := "2.12.18"

lazy val commonSettings = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  scalacOptions += "-language:experimental.macros"
)

lazy val macros = (project in file("macros"))
  .settings(
    commonSettings,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
  )

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "16.0.0-R25",
      "org.scalafx" %% "scalafxml-core-sfx8" % "0.5",
      "org.scalatest" %% "scalatest" % "3.2.9" % Test,
      "org.reflections" % "reflections" % "0.10.2"
    ),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources"
  )
  .dependsOn(macros)
