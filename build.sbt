lazy val root = (project in file(".")).
  aggregate(plugin, extension)

lazy val plugin = project

lazy val extension = project.dependsOn(plugin)
