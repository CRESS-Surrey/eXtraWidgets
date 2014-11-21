lazy val root = (project in file("."))
  .aggregate(xw, core, api, note, checkbox, slider, chooser, multichooser, input, button)

lazy val api = project

lazy val core = project.dependsOn(api)

lazy val xw = project.dependsOn(core)

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
