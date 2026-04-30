ThisBuild / version      := "3.0.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.7.0"
ThisBuild / scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Werror",
  "-encoding", "UTF8")

lazy val root = (project in file("."))
  .aggregate(api, core, note, checkbox, slider, chooser, multichooser, input, button, xw)

lazy val api = project

lazy val note = (project in file("./xw/widgets/NoteWidget/"))
  .dependsOn(api)

lazy val checkbox = (project in file("./xw/widgets/CheckboxWidget/"))
  .dependsOn(api)

lazy val slider = (project in file("./xw/widgets/SliderWidget/"))
  .dependsOn(api)

lazy val chooser = (project in file("./xw/widgets/ChooserWidget/"))
  .dependsOn(api)

lazy val multichooser = (project in file("./xw/widgets/MultiChooserWidget/"))
  .dependsOn(api)

lazy val input = (project in file("./xw/widgets/InputWidgets/"))
  .dependsOn(api)

lazy val button = (project in file("./xw/widgets/ButtonWidget/"))
  .dependsOn(api)

lazy val core = project
  .dependsOn(api)

lazy val xw = project
  .dependsOn(core)
