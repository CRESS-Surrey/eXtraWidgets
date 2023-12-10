name := "eXtraWidgets-API"

scalaVersion := "2.12.18"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

exportJars := true

Compile / artifactName := { (_, _, artifact: Artifact) => artifact.name + "." + artifact.extension }

Test / artifactName := { (_, _, artifact: Artifact) => artifact.name + "-test." + artifact.extension }

resolvers += "netlogo" at "https://dl.cloudsmith.io/public/netlogo/netlogo/maven/"

libraryDependencies ++= Seq(
  "org.nlogo" % "netlogo" % "6.4.0",
  "org.jogamp.jogl" % "jogl-all" % "2.4.0" from "https://jogamp.org/deployment/v2.4.0/jar/jogl-all.jar",
  "org.jogamp.gluegen" % "gluegen-rt" % "2.4.0" from "https://jogamp.org/deployment/v2.4.0/jar/gluegen-rt.jar"
)
