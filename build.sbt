lazy val root = (project in file("."))
  .aggregate(extension, core, api, note, checkbox, slider)
  .dependsOn(core) // dummy dependency, for jar copying to work

lazy val api = project

lazy val core = project.dependsOn(api)

lazy val extension = project.dependsOn(core)

lazy val note = (project in file("./extension/widgets/NoteWidget/"))
  .dependsOn(api)

lazy val checkbox = (project in file("./extension/widgets/CheckboxWidget/"))
  .dependsOn(api)

lazy val slider = (project in file("./extension/widgets/SliderWidget/"))
  .dependsOn(api)

// copy the dependencies of core in plugin dir:
packageBin in Compile <<= (packageBin in Compile, baseDirectory, dependencyClasspath in Runtime) map {
  (jar, base, classPath) =>
    for {
      file <- classPath.files
      fileName = file.getName
      if fileName.endsWith(".jar")
      if !fileName.startsWith("scala-library")
      if !fileName.startsWith("NetLogo")
    } IO.copyFile(file, base / "extension" / fileName)
    jar
}
