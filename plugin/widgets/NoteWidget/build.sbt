name := "NoteWidget"

scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "UTF8")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.1.0" from
    "http://ccl.northwestern.edu/netlogo/5.1.0/NetLogo.jar"
)

packageOptions += Package.ManifestAttributes(
  ("Widget-Kind", "Note"),
  ("Class-Name", "uk.ac.surrey.soc.cress.extrawidgets.note.Note"),
  ("eXtraWidgets-API-Version", "1.0")
)

val jarName = "NoteWidget.jar"

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
    IO.copyFile(jar, base / jarName)
    jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
