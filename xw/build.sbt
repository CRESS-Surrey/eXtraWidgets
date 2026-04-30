enablePlugins(org.nlogo.build.NetLogoExtension)

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
