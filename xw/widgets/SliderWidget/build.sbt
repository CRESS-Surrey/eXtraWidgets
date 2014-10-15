name := "SliderWidget"

scalaVersion := "2.9.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

packageOptions += Package.ManifestAttributes(
  ("Class-Name", "uk.ac.surrey.soc.cress.extrawidgets.slider.Slider"),
  ("eXtraWidgets-API-Version", "1.0")
)

exportJars := true

val jarName = "SliderWidget.jar"

artifactName := { (_, _, _) => jarName }
