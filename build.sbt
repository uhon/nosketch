import sbt.Project.projectToRef

name := """nosketch"""

version := "1.0-SNAPSHOT"

lazy val clients = Seq(nosketchJS)
lazy val scalaV = "2.11.7"


lazy val nosketch = crossProject.
  crossType(NosketchCrossType).
  settings(
    name          := "nosketch",
    scalaVersion  := "2.11.7",
    scalacOptions += "-feature"
  ).jvmSettings(
    initialCommands in console := """
                                    |import nosketch._
                                  """.trim.stripMargin,
    cleanupCommands in console := """
                                    |doodle.jvm.quit()
                                  """.trim.stripMargin
  ).jsSettings(
    workbenchSettings : _*

  ).jsSettings(
    persistLauncher         := true,
    persistLauncher in Test := false,
    bootSnippet             := """
                                 |nosketch.Viewer.main();
                               """.trim.stripMargin,
    testFrameworks          += new TestFramework("utest.runner.Framework"),
    libraryDependencies    ++= Seq(
      "org.scalaz"                %%  "scalaz-core" % "7.1.0",
      "org.scala-js"              %%% "scalajs-dom" % "0.8.1",
      "com.lihaoyi"               %%% "utest"       % "0.3.0" % "test",
      "com.github.japgolly.nyaya" %%% "nyaya-test"  % "0.5.3" % "test"
    ),
    refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJSDependencies in Compile)
  )



lazy val nosketchJVM = (project in file("jvm")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "org.webjars" % "bootstrap" % "3.3.5",
    specs2 % Test
  )
).enablePlugins(PlayScala)
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(nosketchSharedJvm)

lazy val nosketchJS = (project in file("js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  //refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile),
  persistLauncher in Test := false,
  sourceMapsDirectories += nosketchSharedJs.base / "..",
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "org.scala-js" %%% "scalajs-tools" % "0.6.0",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
    "com.lihaoyi" %%% "scalarx" % "0.2.8"
  ),
  jsDependencies ++= Seq(
    RuntimeDOM,
    "org.webjars" % "jquery" % "2.1.4" / "jquery.js",
    "org.webjars" % "bootstrap" % "3.3.5" / "bootstrap.js"
  ),
  persistLauncher in Compile := true,
  skip in packageJSDependencies := false

).enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(nosketchSharedJs)
  .dependsOn(paperScalaJs)

lazy val paperScalaJs = (project in file("paper-scala-js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1" withJavadoc(),
    "org.scala-js" %%% "scalajs-tools" % "0.6.5" withJavadoc()
  ),
  jsDependencies ++= Seq(
    "org.webjars" % "paperjs" % "0.9.22" / "paper-full.min.js" commonJSName "paper"
  ),
  persistLauncher in Compile := false,
  skip in packageJSDependencies := false
)
  .enablePlugins(ScalaJSPlugin)


lazy val nosketchShared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val nosketchSharedJvm = nosketchShared.jvm
lazy val nosketchSharedJs = nosketchShared.js


//lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"


// Scala-Js Workbench (Live-Reload and such things)
//workbenchSettings

//bootSnippet := "nosketch.Viewer.startViewer(document.getElementById('canvas'));"

scalaJSStage in Global := FastOptStage


persistLauncher in Compile := false

persistLauncher in Test := false

skip in packageJSDependencies := false


// loads the Play project at sbt startup
onLoad in Global := (Command.process("project nosketchJVM", _: State)) compose (onLoad in Global).value

