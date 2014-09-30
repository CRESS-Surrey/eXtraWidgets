lazy val root = (project in file(".")).
  aggregate(plugin, extension)

lazy val state = project

lazy val plugin = project.dependsOn(state)

lazy val extension = project.dependsOn(state)
