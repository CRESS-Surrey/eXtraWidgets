lazy val root = (project in file("."))
  .aggregate(xw, core, api, note, checkbox, slider)

lazy val api = project

lazy val core = project.dependsOn(api)

lazy val xw = project.dependsOn(core)

lazy val note = (project in file("./xw/widgets/NoteWidget/"))
  .dependsOn(api)

lazy val checkbox = (project in file("./xw/widgets/CheckboxWidget/"))
  .dependsOn(api)

lazy val slider = (project in file("./xw/widgets/SliderWidget/"))
  .dependsOn(api)
