name := "ButtonWidget"

artifactName := { (_, _, _) => name.value + ".jar" }

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
