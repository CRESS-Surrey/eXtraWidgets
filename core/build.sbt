name := "eXtraWidgets-Core"

scalaVersion := "2.9.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding", "UTF8")

exportJars := true

crossPaths := false

fork := true

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.1.0" from
    "http://ccl.northwestern.edu/netlogo/5.1.0/NetLogo.jar",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1"
)

libraryDependencies ++= Seq( // test libraries
 "org.scalatest" %% "scalatest" % "1.9.2" % "test",
 "com.typesafe.akka" % "akka-actor" % "2.0.5" % "test",
 "org.picocontainer" % "picocontainer" % "2.13.6" % "test",
 "log4j" % "log4j" % "1.2.16" % "test",
 "org.jhotdraw" % "jhotdraw" % "6.0b1" % "test" from "http://ccl.northwestern.edu/devel/jhotdraw-6.0b1.jar",
 "org.apache.httpcomponents" % "httpclient" % "4.2" % "test",
 "org.pegdown" % "pegdown" % "1.1.0" % "test"
)
