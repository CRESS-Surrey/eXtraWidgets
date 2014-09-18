name := "eXtraWidgetsPlugin"

scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "UTF8")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.1.0" from
    "http://ccl.northwestern.edu/netlogo/5.1.0/NetLogo.jar"
)

packageOptions += Package.ManifestAttributes(
  ("Tab-Name", "eXtraWidgets"),
  ("Class-Name", "uk.ac.surrey.soc.cress.extrawidgets.plugin.ExtraWidgetsPlugin"),
  ("NetLogo-API-Version", "5.0")
)

val jarName = "eXtraWidgets.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
    IO.copyFile(jar, base / jarName)
    jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
