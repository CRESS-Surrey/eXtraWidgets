name := "eXtraWidgetsPlugin"

scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "UTF8")

resolvers ++= Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.1.0" from
    "http://ccl.northwestern.edu/netlogo/5.1.0/NetLogo.jar",
  "org.scalatest" %% "scalatest" % "1.9.2" % "test",
  "com.typesafe.akka" % "akka-actor" % "2.0.5" % "test"
)

// Dependencies for NetLogo controlling in tests
libraryDependencies ++= Seq(
  "asm" % "asm-all" % "3.3.1" % "test",
  "org.picocontainer" % "picocontainer" % "2.13.6" % "test",
  "log4j" % "log4j" % "1.2.16" % "test",
  "javax.media" % "jmf" % "2.1.1e" % "test",
  "org.pegdown" % "pegdown" % "1.1.0" % "test",
  "org.parboiled" % "parboiled-java" % "1.0.2" % "test",
  "steveroy" % "mrjadapter" % "1.2" % "test"
    from "http://ccl.northwestern.edu/devel/mrjadapter-1.2.jar",
  "org.jhotdraw" % "jhotdraw" % "6.0b1" % "test"
    from "http://ccl.northwestern.edu/devel/jhotdraw-6.0b1.jar",
  "ch.randelshofer" % "quaqua" % "7.3.4" % "test"
    from "http://ccl.northwestern.edu/devel/quaqua-7.3.4.jar",
  "ch.randelshofer" % "swing-layout" % "7.3.4" % "test"
    from "http://ccl.northwestern.edu/devel/swing-layout-7.3.4.jar",
  "org.jogl" % "jogl" % "1.1.1" % "test"
    from "http://ccl.northwestern.edu/devel/jogl-1.1.1.jar",
  "org.gluegen-rt" % "gluegen-rt" % "1.1.1" % "test"
    from "http://ccl.northwestern.edu/devel/gluegen-rt-1.1.1.jar",
  "org.apache.httpcomponents" % "httpclient" % "4.2"  % "test",
  "org.apache.httpcomponents" % "httpmime" % "4.2"  % "test",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1"  % "test"
)

packageOptions += Package.ManifestAttributes(
  ("Tab-Name", "eXtraWidgets"),
  ("Class-Name", "uk.ac.surrey.soc.cress.extrawidgets.plugin.ExtraWidgetsPlugin"),
  ("NetLogo-API-Version", "5.0")
)

val jarName = "eXtraWidgets.jar"

artifactName := { (_, _, _) => jarName }

packageBin in Compile <<= (packageBin in Compile, baseDirectory) map {
  (jar, base) =>
    IO.copyFile(jar, base / jarName)
    IO.copyFile(jar, base / "extension-lib" / jarName)
    jar
}

cleanFiles <++= baseDirectory { base => Seq(base / jarName) }
