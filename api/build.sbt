name := "eXtraWidgets-API"

scalaVersion := "2.12.18"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

exportJars := true

artifactName in Compile := { (_, _, artifact: Artifact) => artifact.name + "." + artifact.extension }

artifactName in Test := { (_, _, artifact: Artifact) => artifact.name + "-test." + artifact.extension }

resolvers += "netlogo" at "https://dl.cloudsmith.io/public/netlogo/netlogo/maven/"

libraryDependencies ++= Seq(
  "org.nlogo" % "netlogo" % "6.4.0" intransitive
)
