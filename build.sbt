lazy val root = (project in file("."))
  .aggregate(extension, core, api, note, checkbox, slider)

lazy val api = project

lazy val core = project.dependsOn(api)

lazy val extension = project.dependsOn(core)

lazy val note = (project in file("./extension/widgets/NoteWidget/"))
  .dependsOn(api)

lazy val checkbox = (project in file("./extension/widgets/CheckboxWidget/"))
  .dependsOn(api)

lazy val slider = (project in file("./extension/widgets/SliderWidget/"))
  .dependsOn(api)
