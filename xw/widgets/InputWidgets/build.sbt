name := "InputWidgets"

scalaVersion := "2.9.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

exportJars := true

val jarName = "InputWidgets.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
  IO.copyFile(jar, base / jarName)
  jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
