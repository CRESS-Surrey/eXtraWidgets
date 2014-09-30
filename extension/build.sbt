scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "UTF8")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.1.0"
    from "http://ccl.northwestern.edu/netlogo/5.1.0/NetLogo.jar"
)

name := "eXtraWidgets-Extension"

val jarName = "xw.jar"

artifactName := { (_, _, _) => jarName }

packageOptions += Package.ManifestAttributes(
  ("Extension-Name", "xw"),
  ("Class-Manager", "uk.ac.surrey.soc.cress.extrawidgets.extension.ExtraWidgetsExtension"),
  ("NetLogo-Extension-API-Version", "5.0")
)

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
    IO.copyFile(jar, base / jarName)
    jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
