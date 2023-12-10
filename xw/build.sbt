enablePlugins(org.nlogo.build.NetLogoExtension)

name                    := "eXtraWidgets Extension"
netLogoExtName          := "xw"
netLogoClassManager     := "uk.ac.surrey.xw.extension.ExtraWidgetsExtension"
netLogoVersion          := "6.4.0"
netLogoShortDescription := "An extension for creating additional interface tabs in the NetLogo GUI and putting custom widgets on them."
netLogoLongDescription  := netLogoShortDescription.value
netLogoHomepage         := "https://github.com/NetLogo/NetLogo-Extension-Plugin"

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