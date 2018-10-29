lazy val root = (project in file("."))
  .settings(version := "2.0.0")
  .aggregate(xw, core, api, note, image, checkbox, slider, chooser, multichooser, input, button)

lazy val api = project
  .settings(version := "2.0.0")

lazy val core = project
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val note = (project in file("./xw/widgets/NoteWidget/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val image = (project in file("./xw/widgets/ImageWidget/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val checkbox = (project in file("./xw/widgets/CheckboxWidget/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val slider = (project in file("./xw/widgets/SliderWidget/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val chooser = (project in file("./xw/widgets/ChooserWidget/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val multichooser = (project in file("./xw/widgets/MultiChooserWidget/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val input = (project in file("./xw/widgets/InputWidgets/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val button = (project in file("./xw/widgets/ButtonWidget/"))
  .settings(version := "2.0.0")
  .dependsOn(api)

lazy val xw = project.dependsOn(core)
  .settings(
    version := "2.0.0",
    (test in Test) := {
      val _ = Seq(
        (Keys.`package` in (note, Compile)).value,
        (Keys.`package` in (image, Compile)).value,
        (Keys.`package` in (checkbox, Compile)).value,
        (Keys.`package` in (slider, Compile)).value,
        (Keys.`package` in (chooser, Compile)).value,
        (Keys.`package` in (multichooser, Compile)).value,
        (Keys.`package` in (input, Compile)).value,
        (Keys.`package` in (button, Compile)).value
      )
      (test in Test).value
    }
  )
