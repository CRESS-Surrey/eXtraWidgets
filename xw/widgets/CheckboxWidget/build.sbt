name := "CheckboxWidget"

scalaVersion := "2.9.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

val jarName = "CheckboxWidget.jar"

artifactName := { (_, _, _) => jarName }
