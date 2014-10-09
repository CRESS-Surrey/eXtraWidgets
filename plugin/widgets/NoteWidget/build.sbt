name := "NoteWidget"

scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "UTF8")

packageOptions += Package.ManifestAttributes(
  ("Class-Name", "uk.ac.surrey.soc.cress.extrawidgets.note.Note"),
  ("eXtraWidgets-API-Version", "1.0")
)

exportJars := true

val jarName = "NoteWidget.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
    IO.copyFile(jar, base / jarName)
    jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
