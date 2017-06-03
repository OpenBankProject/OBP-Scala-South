logLevel := Level.Warn

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.1")
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.0.2")
addSbtPlugin("com.github.retronym" % "sbt-onejar" % "0.8")
