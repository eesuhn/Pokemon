import sbtassembly.AssemblyPlugin.autoImport._

name := "Pokemon"

version := "0.1"

scalaVersion := "2.12.18"

lazy val commonSettings = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  scalacOptions ++= Seq(
    "-language:experimental.macros",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-Xlint:unused"
  )
)

lazy val macros = (project in file("macros"))
  .settings(
    commonSettings,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
  )

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Mac") => 
    System.getProperty("os.arch") match {
      case "aarch64" | "arm64" => "mac-aarch64"
      case _ => "mac"
    }
  case n if n.startsWith("Win") => "win"
  case _ => "linux"
}

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "18.0.1-R27",
      "org.scalafx" %% "scalafxml-core-sfx8" % "0.5",
      "org.scalatest" %% "scalatest" % "3.2.9" % Test,
      "org.reflections" % "reflections" % "0.10.2",

      "org.openjfx" % "javafx-base" % "18.0.2" classifier osName,
      "org.openjfx" % "javafx-controls" % "18.0.2" classifier osName,
      "org.openjfx" % "javafx-fxml" % "18.0.2" classifier osName,
      "org.openjfx" % "javafx-graphics" % "18.0.2" classifier osName,
      "org.openjfx" % "javafx-media" % "18.0.2" classifier osName
    ),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources",
    assembly / mainClass := Some("pokemon.MainApp"),
    assembly / assemblyJarName := "Pokemon.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) =>
        (xs map {_.toLowerCase}) match {
          case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
            MergeStrategy.discard
          case _ => MergeStrategy.first
        }
      case "module-info.class" => MergeStrategy.discard
      case x if x.endsWith(".json") => MergeStrategy.first
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  )
  .dependsOn(macros)

inThisBuild(
  List(
    scalaVersion := "2.12.18",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / scalafixDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.9"
)
