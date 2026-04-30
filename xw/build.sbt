enablePlugins(org.nlogo.build.NetLogoExtension)

name                    := "eXtraWidgets Extension"
netLogoExtName          := "xw"
netLogoClassManager     := "uk.ac.surrey.xw.extension.ExtraWidgetsExtension"
netLogoVersion          := "6.4.0"
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

netLogoPackageExtras ++=
  Seq("api", "core")
    .map(x =>
      (baseDirectory.value / ".." / x / "target" / "scala-2.12" / s"extrawidgets-$x.jar", None)
    ) ++
    (baseDirectory.value / "widgets")
      .listFiles
      .filter(_.isDirectory)
      .map(widgetFolder => {
        val name = widgetFolder.getName
        val jar = widgetFolder / "target" / "scala-2.12" / (name + ".jar")
        (jar, Option("widgets/" + name + "/" + jar.getName))
      })
