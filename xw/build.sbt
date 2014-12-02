scalaVersion := "2.9.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.1.0"
    from "http://ccl.northwestern.edu/netlogo/5.1.0/NetLogo.jar"
)

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo-tests" % "5.0.5" % "test" from
    "http://ccl.northwestern.edu/netlogo/5.1/NetLogo-tests.jar",
  "org.scalatest" % "scalatest_2.9.2" % "1.8" % "test",
  "org.picocontainer" % "picocontainer" % "2.13.6" % "test",
  "asm" % "asm-all" % "3.3.1" % "test"
)

name := "eXtraWidgets-Extension"

val jarName = "xw.jar"

artifactName := { (_, _, _) => jarName }

packageOptions += Package.ManifestAttributes(
  ("Extension-Name", "xw"),
  ("Class-Manager", "uk.ac.surrey.xw.extension.ExtraWidgetsExtension"),
  ("NetLogo-Extension-API-Version", "5.0")
)

test in Test := {
  val _ = (packageBin in Compile).value
  (test in Test).value
}

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
