scalaVersion := "2.12.2"

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

netLogoVersion := "6.1.1"

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
    IO.copyFile((packageBin in Compile).value, baseDirectory.value / jarName)
    val jarFiles = (dependencyClasspath in Runtime).value.files
      .filter { _.getName matches "(.*).jar" }
      .filterNot { _.getName matches "netlogo(.*).jar" }
      .filterNot { _.getName matches "scala-library(.*).jar" }
    jarFiles.foreach(file => IO.copyFile(file, baseDirectory.value / file.getName))
    // copy everything thing we need for distribution in a
    // temp "xw" directory, which we will zip before deleting it.
    IO.createDirectory(baseDirectory.value / "xw")
    val fileNames = jarFiles.map(_.getName) :+ "xw.jar"
    for (fn <- fileNames) IO.copyFile(baseDirectory.value / fn, baseDirectory.value / "xw" / fn)
    IO.createDirectory(baseDirectory.value / "xw" / "widgets")
    for {
      path <- (baseDirectory.value / "widgets").listFiles
      if path.isDirectory
      file <- path.listFiles
      if file.getName.endsWith(".jar")
    } {
      IO.createDirectory(baseDirectory.value / "xw" / "widgets" / path.getName)
      IO.copyFile(file, baseDirectory.value / "xw" / "widgets" / path.getName / file.getName)
    }
    IO.delete(baseDirectory.value / "xw.zip")
    scala.sys.process.Process(Seq("zip", "-r", "xw.zip", "xw"), baseDirectory.value).!!
    IO.delete(baseDirectory.value / "xw")
    (packageBin in Compile).value
}

cleanFiles += baseDirectory.value / jarName
