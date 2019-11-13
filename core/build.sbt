name := "eXtraWidgets-Core"

scalaVersion := "2.12.0"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

exportJars := true

artifactName in Compile := { (_, _, artifact: Artifact) => artifact.name + "." + artifact.extension }

artifactName in Test := { (_, _, artifact: Artifact) => artifact.name + "-test." + artifact.extension }

fork := true

libraryDependencies ++= Seq(
  "com.github.cliftonlabs" % "json-simple" % "2.3.0"
)
