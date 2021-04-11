
// Comment to get more information during initialization
logLevel := Level.Warn

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "spray repo" at "https://repo.spray.io"

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.10")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.2")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.2.2")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.0.0")

addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.0.0")

addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.3.7")


//addSbtPlugin("com.lihaoyi" % "workbench" % "0.2.3")

lazy val root = project.in(file("."))
lazy val workbenchPlugin = uri("git://github.com/lihaoyi/workbench")

