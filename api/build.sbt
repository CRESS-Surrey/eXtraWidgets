name := "eXtraWidgets-API"

scalaVersion := "2.9.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

exportJars := true

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "." + artifact.extension
}

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.1.0" from
    "http://ccl.northwestern.edu/netlogo/5.1.0/NetLogo.jar"
)

site.settings

site.includeScaladoc("/")

ghpages.settings

git.remoteRepo := "git@github.com:CRESS-Surrey/eXtraWidgets.git"
