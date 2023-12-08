name := "InputWidgets"

scalaVersion := "2.12.18"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

val jarName = "InputWidgets.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile := {
  val jar = (packageBin in Compile).value
  IO.copyFile(jar, baseDirectory.value / jarName)
  jar
}

cleanFiles += baseDirectory.value / jarName
