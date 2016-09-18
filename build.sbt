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
//  ).jsSettings(
//    workbenchSettings : _*

  ).jsSettings(
    persistLauncher         := true,
    persistLauncher in Test := false,
//    bootSnippet             := """
//                                 |nosketch.Viewer3D().reset();
//                               """.trim.stripMargin,
    testFrameworks          += new TestFramework("utest.runner.Framework"),
    libraryDependencies    ++= Seq(
      "org.scalaz"                %%  "scalaz-core" % "7.1.0",
      "org.scala-js"              %%% "scalajs-dom" % "0.9.0",
      "com.lihaoyi"               %%% "utest"       % "0.3.0" % "test",
      "com.github.japgolly.nyaya" %%% "nyaya-test"  % "0.5.3" % "test"
    )
//    updateBrowsers <<= updateBrowsers.triggeredBy(packageJSDependencies in Compile)
  )



lazy val nosketchJVM = (project in file("jvm")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.4.0",
    "org.webjars" % "bootstrap" % "3.3.5",
    "org.webjars" % "font-awesome" % "4.4.0",
    filters
  )
).enablePlugins(PlayScala)
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(nosketchSharedJvm)

lazy val vonGridScalaJs = (project in file("von-grid-scala-js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  //refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile),
  persistLauncher in Test := false,
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "org.scala-js" %%% "scalajs-tools" % "0.6.6",
    "org.querki" %%% "jquery-facade" % "1.0-RC2", //scalajs facade for jQuery + jQuery extensions
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    //    "org.denigma" %%% "threejs-facade" % "0.0.74-0.1.6",
    "org.querki" %%% "querki-jsext" % "0.7"
//    "org.denigma" %%% "threejs-facade" % "0.0.74-0.1.6" //add dependency
  ),
  jsDependencies ++= Seq(
    RuntimeDOM
//    "org.webjars" % "three.js" % "r77" / "three.js"
//    "org.webjars.npm" % "three-orbit-controls" % "69.0.5" / "index.js"
  ),
  persistLauncher in Compile := true,
  skip in packageJSDependencies := false

).dependsOn(nosketchSharedJs)
  .dependsOn(threejsFacade)
  .enablePlugins(ScalaJSPlugin)

lazy val nosketchJS = (project in file("js"))
  .settings(workbenchSettings: _*)
  .settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  //refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile),
  persistLauncher in Test := false,
  sourceMapsDirectories += nosketchSharedJs.base / "..",
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "org.scala-js" %%% "scalajs-tools" % "0.6.6",
    "org.querki" %%% "jquery-facade" % "1.0-RC2", //scalajs facade for jQuery + jQuery extensions
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
//    "org.denigma" %%% "threejs-facade" % "0.0.74-0.1.6",
    "org.querki" %%% "querki-jsext" % "0.7",
    "com.lihaoyi" %%% "scalatags" % "0.6.0"
  ),
  jsDependencies ++= Seq(
    RuntimeDOM,
    "org.webjars" % "jquery" % "2.2.1" / "jquery.js",
    "org.webjars" % "bootstrap" % "3.3.5" / "bootstrap.js"
//    "org.webjars" % "three.js" % "r77" / "three.js"
//    "org.webjars.npm" % "three-orbit-controls" % "69.0.5" / "index.js"
  ),
  persistLauncher in Compile := true,
  // Scala-Js Workbench (Live-Reload and such things)
  bootSnippet := "nosketch.Viewer3D().reset();",
  localUrl := ("127.0.0.1", 12345),
  updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)
).enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(nosketchSharedJs)
  .dependsOn(vonGridScalaJs)
  .dependsOn(paperScalaJs)
  .dependsOn(threejsFacade)

lazy val paperScalaJs = (project in file("paper-scala-js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0" withJavadoc(),
    "org.scala-js" %%% "scalajs-tools" % "0.6.6" withJavadoc()
  ),
//  jsDependencies ++= Seq(
//    "org.webjars" % "paperjs" % "0.9.24" / "paper-full.min.js" commonJSName "paper"
//  ),
  persistLauncher in Compile := false,
  skip in packageJSDependencies := false
)
  .enablePlugins(ScalaJSPlugin)

lazy val threejsFacade = (project in file("threejs-facade/facade")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0" withJavadoc()
//    "org.scala-js" %%% "scalajs-tools" % "0.6.6" withJavadoc()
  ),
  //  jsDependencies ++= Seq(
  //    "org.webjars" % "paperjs" % "0.9.24" / "paper-full.min.js" commonJSName "paper"
  //  ),
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




skip in packageJSDependencies := false



// Resolve only newly added dependencies
updateOptions := updateOptions.value.withCachedResolution(true)

//updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)


//
//scalaJSStage in Global := FastOptStage


persistLauncher in Compile := false

persistLauncher in Test := false

skip in packageJSDependencies := false


// loads the Play project at sbt startup
onLoad in Global := (Command.process("project nosketchJVM", _: State)) compose (onLoad in Global).value

