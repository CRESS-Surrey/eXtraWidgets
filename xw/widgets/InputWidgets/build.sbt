name := "InputWidgets"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

val jarName = "InputWidgets.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
  IO.copyFile(jar, base / jarName)
  jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
