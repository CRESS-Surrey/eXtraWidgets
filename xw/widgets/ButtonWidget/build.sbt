name := "ButtonWidget"

scalaVersion := "2.12.2"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

val jarName = "ButtonWidget.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
  IO.copyFile(jar, base / jarName)
  jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
