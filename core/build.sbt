name := "eXtraWidgets-Core"

exportJars := true

Compile / artifactName := { (_, _, artifact: Artifact) => artifact.name + "." + artifact.extension }

Test / artifactName := { (_, _, artifact: Artifact) => artifact.name + "-test." + artifact.extension }

fork := true

libraryDependencies ++= Seq(
  "com.github.cliftonlabs" % "json-simple" % "2.3.0"
)
