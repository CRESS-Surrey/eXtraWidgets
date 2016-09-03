scalaVersion := "2.11.8"

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

netLogoVersion := "6.0.0-BETA1"

test in Test := {
  val _ = (packageBin in Compile).value
  (test in Test).value
}

val jarName = "xw.jar"

packageBin in Compile <<= (packageBin in Compile, baseDirectory, dependencyClasspath in Runtime) map {
  (jar, base, classPath) =>
    IO.copyFile(jar, base / jarName)
    val jarFiles = for {
      file <- classPath.files
      fileName = file.getName
      if fileName.endsWith(".jar")
      if !fileName.startsWith("scala-library")
      if !fileName.startsWith("NetLogo")
    } yield file
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
    Process(Seq("zip", "-r", "xw.zip", "xw"), base).!!
    IO.delete(base / "xw")
    jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
