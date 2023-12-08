scalaVersion := "2.12.18"

enablePlugins(org.nlogo.build.NetLogoExtension)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

name := "eXtraWidgets-Extension"

netLogoExtName := "xw"

netLogoClassManager := "uk.ac.surrey.xw.extension.ExtraWidgetsExtension"

netLogoZipSources := false

netLogoTarget :=
    org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

netLogoVersion := "6.4.0"

libraryDependencies ++= Seq(
  "org.ow2.asm" % "asm-all" % "5.0.4" % "test",
  "org.picocontainer" % "picocontainer" % "2.13.6" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "commons-codec" % "commons-codec" % "1.10" % "test"
)

test in Test := {
  val _ = (packageBin in Compile).value
  (test in Test).value
}

val jarName = "xw.jar"

packageBin in Compile := {
  val jar = (packageBin in Compile).value
  val base = baseDirectory.value
  val classPath = (dependencyClasspath in Runtime).value
  IO.copyFile(jar, base / jarName)
  val jarFiles = classPath.files
    .filter { _.getName matches "(.*).jar" }
    .filterNot { _.getName matches "netlogo(.*).jar" }
    .filterNot { _.getName matches "scala-library(.*).jar" }
  jarFiles.foreach(file => IO.copyFile(file, base / file.getName))
  // copy everything thing we need for distribution in a
  // temp "xw" directory, which we will zip before deleting it.
  IO.createDirectory(base / "xw")
  val fileNames = jarFiles.map(_.getName) :+ "xw.jar"
  for (fn <- fileNames) IO.copyFile(base / fn, base / "xw" / fn)
  IO.createDirectory(base / "xw" / "widgets")
  for {
    path <- (base / "widgets").listFiles
    if path.isDirectory
    file <- path.listFiles
    if file.getName.endsWith(".jar")
  } {
    IO.createDirectory(base / "xw" / "widgets" / path.getName)
    IO.copyFile(file, base / "xw" / "widgets" / path.getName / file.getName)
  }
  IO.delete(base / "xw.zip")
  scala.sys.process.Process(Seq("zip", "-r", "xw.zip", "xw"), base).!!
  IO.delete(base / "xw")
  jar
}

cleanFiles += baseDirectory.value / jarName
