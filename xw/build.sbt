enablePlugins(org.nlogo.build.NetLogoExtension)

import java.io.File
import java.util.concurrent.TimeUnit

name                    := "eXtraWidgets Extension"
netLogoExtName          := "xw"
netLogoClassManager     := "uk.ac.surrey.xw.extension.ExtraWidgetsExtension"
netLogoVersion          := "7.0.3"
netLogoShortDescription := "An extension for creating additional interface tabs in the NetLogo GUI and putting custom widgets on them."
netLogoLongDescription  := netLogoShortDescription.value
netLogoHomepage         := "https://github.com/NetLogo/NetLogo-Extension-Plugin"

libraryDependencies ++= Seq(
  "org.jogamp.jogl" % "jogl-all" % "2.4.0" from "https://jogamp.org/deployment/v2.4.0/jar/jogl-all.jar",
  "org.jogamp.gluegen" % "gluegen-rt" % "2.4.0" from "https://jogamp.org/deployment/v2.4.0/jar/gluegen-rt.jar"
)

Compile / packageBin := (Compile / packageBin).dependsOn(
  LocalProject("api") / Compile / packageBin,
  LocalProject("core") / Compile / packageBin,
  LocalProject("note") / Compile / packageBin,
  LocalProject("checkbox") / Compile / packageBin,
  LocalProject("slider") / Compile / packageBin,
  LocalProject("chooser") / Compile / packageBin,
  LocalProject("multichooser") / Compile / packageBin,
  LocalProject("input") / Compile / packageBin,
  LocalProject("button") / Compile / packageBin
).value

lazy val packagedSmoke = taskKey[Unit](
  "Build the xw zip, install it into a temporary NetLogo extensions folder, and smoke-test that installed copy.")

packagedSmoke := {
  val log = streams.value.log

  // This task is deliberately separate from `test`.  Most NetLogo extensions
  // are a single jar with static primitives, so their sbt test classpath is a
  // good approximation of an installed extension.  xw is not that simple: it
  // loads widget kinds from jars under `widgets/`, and `prims.json` generation
  // has to discover those dynamic primitives.  A normal language test can pass
  // while the packaged zip is missing a jar or cannot be found through
  // NetLogo's installed-extension lookup.
  val zip = packageZip.value
  (Test / compile).value

  val repoRoot = baseDirectory.value.getParentFile.getCanonicalFile.toPath
  val testClasses = (Test / classDirectory).value.getCanonicalFile
  val dependencyClasspath = (Test / fullClasspath).value.files
    .map(_.getCanonicalFile)
    // Keep NetLogo and third-party dependency jars, but exclude repo-built xw
    // jars/classes.  The smoke runner itself comes from `testClasses`; the xw
    // extension under test must come only from the unpacked zip below.
    .filterNot(file => file.toPath.startsWith(repoRoot))
  val classpath = (testClasses +: dependencyClasspath)
    .distinct
    .map(_.getAbsolutePath)
    .mkString(File.pathSeparator)

  val tempDir = IO.createTemporaryDirectory
  try {
    val extensionsDir = tempDir / "extensions"
    val xwDir = extensionsDir / "xw"
    IO.unzip(zip, xwDir)

    val output = new StringBuilder
    val java = file(System.getProperty("java.home")) / "bin" / "java"
    val processBuilder = new ProcessBuilder(
      java.getAbsolutePath,
      "-cp",
      classpath,
      "uk.ac.surrey.xw.extension.PackagedSmokeRunner",
      extensionsDir.getAbsolutePath)
    processBuilder.redirectErrorStream(true)

    val process = processBuilder.start()
    val outputThread = new Thread(new Runnable {
      def run(): Unit = {
        val source = scala.io.Source.fromInputStream(process.getInputStream)
        try source.getLines.foreach(line => output.append(line).append('\n'))
        finally source.close()
      }
    }, "xw-packaged-smoke-output")
    outputThread.setDaemon(true)
    outputThread.start()

    val timeoutSeconds = 60L
    if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
      process.destroyForcibly()
      sys.error(
        s"Packaged smoke test timed out after $timeoutSeconds seconds.\n" +
          output.toString)
    }
    outputThread.join(1000L)

    if (process.exitValue != 0)
      sys.error("Packaged smoke test failed.\n" + output.toString)

    val expectedJar = (xwDir / "xw.jar").getCanonicalPath
    if (!output.toString.contains("xw smoke ok") || !output.toString.contains(expectedJar))
      sys.error("Packaged smoke test did not prove it loaded the unpacked xw.jar.\n" + output.toString)

    log.info(output.toString.trim)
  } finally {
    IO.delete(tempDir)
  }
}

netLogoPackageExtras ++=
  Seq(
    ((LocalProject("api") / Compile / crossTarget).value / "extrawidgets-api.jar") -> None,
    ((LocalProject("core") / Compile / crossTarget).value / "extrawidgets-core.jar") -> None,
    ((LocalProject("note") / Compile / crossTarget).value / "NoteWidget.jar") -> Some("widgets/NoteWidget/NoteWidget.jar"),
    ((LocalProject("checkbox") / Compile / crossTarget).value / "CheckboxWidget.jar") -> Some("widgets/CheckboxWidget/CheckboxWidget.jar"),
    ((LocalProject("slider") / Compile / crossTarget).value / "SliderWidget.jar") -> Some("widgets/SliderWidget/SliderWidget.jar"),
    ((LocalProject("chooser") / Compile / crossTarget).value / "ChooserWidget.jar") -> Some("widgets/ChooserWidget/ChooserWidget.jar"),
    ((LocalProject("multichooser") / Compile / crossTarget).value / "MultiChooserWidget.jar") -> Some("widgets/MultiChooserWidget/MultiChooserWidget.jar"),
    ((LocalProject("input") / Compile / crossTarget).value / "InputWidgets.jar") -> Some("widgets/InputWidgets/InputWidgets.jar"),
    ((LocalProject("button") / Compile / crossTarget).value / "ButtonWidget.jar") -> Some("widgets/ButtonWidget/ButtonWidget.jar")
  )
