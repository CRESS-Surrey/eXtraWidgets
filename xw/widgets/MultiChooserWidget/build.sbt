name := "MultiChooserWidget"

scalaVersion := "2.12.2"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

val jarName = "MultiChooserWidget.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile := {
  val jar = (packageBin in Compile).value
  IO.copyFile(jar, baseDirectory.value / jarName)
  jar
}

cleanFiles += baseDirectory.value / jarName
