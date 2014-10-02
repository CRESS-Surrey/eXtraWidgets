lazy val root = (project in file("."))
  .aggregate(plugin, extension, core, state, api)
  .dependsOn(core) // dummy dependency, for jar copying to work

lazy val plugin = project

lazy val state = project

lazy val api = project.dependsOn(state)

lazy val core = project.dependsOn(state, api, plugin)

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
