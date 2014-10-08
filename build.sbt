lazy val root = (project in file("."))
  .aggregate(plugin, extension, gui, state, api, note)
  .dependsOn(gui) // dummy dependency, for jar copying to work

lazy val plugin = project

lazy val api = project

lazy val state = project.dependsOn(api)

lazy val gui = project.dependsOn(state, api, plugin)

lazy val extension = project.dependsOn(state)

lazy val note = (project in file("./plugin/widgets/NoteWidget/"))
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
    } IO.copyFile(file, base / "plugin" / fileName)
    jar
}
