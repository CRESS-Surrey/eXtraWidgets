addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.4")

resolvers += Resolver.url(
  "NetLogo-JVM",
  url("http://dl.bintray.com/content/netlogo/NetLogo-JVM"))(
    Resolver.ivyStylePatterns)

addSbtPlugin("org.nlogo" % "netlogo-extension-plugin" % "3.0")