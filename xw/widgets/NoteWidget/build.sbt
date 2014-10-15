name := "NoteWidget"

scalaVersion := "2.9.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

exportJars := true

val jarName = "NoteWidget.jar"

artifactName := { (_, _, _) => jarName }
