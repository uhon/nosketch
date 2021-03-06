import sbt.Project.projectToRef
import play.sbt.PlayScala
import sbt._
import Keys._
import play.sbt._
import Play.autoImport._
import PlayKeys._
import org.scalajs.linker.interface.StandardConfig

name := """nosketch"""

version := "1.0-SNAPSHOT"

lazy val clients = Seq(nosketchJS, nosketchWebworker)
lazy val scalaV = "2.13.2"


lazy val nosketch = crossProject(JSPlatform, JVMPlatform).
  crossType(CrossType.Pure).
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
//    persistLauncher         := true,
//    persistLauncher in Test := false,
//    bootSnippet             := """
//                                 |nosketch.Viewer3D().reset();
//                               """.trim.stripMargin,
    testFrameworks          += new TestFramework("utest.runner.Framework"),
    libraryDependencies    ++= Seq(
//      "org.scalaz"                %%  "scalaz-core" % "7.1.0",
      "org.scala-js"              %%% "scalajs-dom" % "1.1.0",
      "com.lihaoyi"               %%% "utest"       % "0.3.0" % "test",
      "com.github.japgolly.nyaya" %%% "nyaya-test"  % "0.5.3" % "test"
    )
//    updateBrowsers <<= updateBrowsers.triggeredBy(packageJSDependencies in Compile)
  )



lazy val nosketchJVM = (project in file("jvm")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
//  pipelineStages in Assets := Seq(scalaJSPipeline),
//  pipelineStages := Seq(scalaJSProd, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.4",
    "org.webjars" % "bootstrap" % "3.3.5",
    "org.webjars" % "font-awesome" % "4.4.0",
    filters,
    guice
  )
).enablePlugins(PlayScala, SbtWeb)
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(nosketchSharedJvm)

lazy val vonGridScalaJs = (project in file("von-grid-scala-js")).settings(
  scalaVersion := scalaV,
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "1.1.0",
//    "org.scala-js" %%% "scalajs-tools" % "1.0.0-M2",
    "org.querki" %%% "jquery-facade" % "2.0", //scalajs facade for jQuery + jQuery extensions
//    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    //    "org.denigma" %%% "threejs-facade" % "0.0.74-0.1.6",
    "org.querki" %%% "querki-jsext" % "0.10"
//    "org.denigma" %%% "threejs-facade" % "0.0.74-0.1.6" //add dependency
  ),
//  jsDependencies ++= Seq(
//    RuntimeDOM
//    "org.webjars" % "three.js" % "r77" / "three.js"
//    "org.webjars.npm" % "three-orbit-controls" % "69.0.5" / "index.js"
//  ),
//  persistLauncher in Compile := false,
  skip in packageJSDependencies := false

)
//  .dependsOn(nosketchSharedJs)
  .dependsOn(threejsFacade)
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)

lazy val nosketchJS = (project in file("js"))
//  .settings(workbenchSettings: _*)
  .settings(
  scalaVersion := scalaV,

//  refreshBrowsers := refreshBrowsers.triggeredBy(fastOptJS in Compile),
//  persistLauncher in Test := false,
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "1.1.0",
//    "org.scala-js" %%% "scalajs-tools" % "1.0.0-M2",
    "org.querki" %%% "jquery-facade" % "2.0", //scalajs facade for jQuery + jQuery extensions
//    "com.lihaoyi" %%% "scalarx" % "0.2.8",
//    "org.denigma" %%% "threejs-facade" % "0.0.74-0.1.6",
    "org.querki" %%% "querki-jsext" % "0.10"
  ),
  jsDependencies ++= Seq(
//    RuntimeDOM,
    "org.webjars" % "jquery" % "2.2.1" / "jquery.js",
    "org.webjars" % "bootstrap" % "3.3.5" / "bootstrap.js",
    ProvidedJS / "bundle.js"
//    "org.webjars" % "three.js" % "r77" / "three.js"
  ),
  scalacOptions ++= Seq(
    "-language:postfixOps"
  ),
//  mainClass := Some("nosketch.Viewer3D"),
  scalaJSUseMainModuleInitializer := true

    // Scala-Js Workbench (Live-Reload and such things)
//  persistLauncher in Compile := true,
//  bootSnippet := "nosketch.Viewer3D().reset();",
//  localUrl := ("127.0.0.1", 12345),
//  refreshBrowsers := refreshBrowsers.triggeredBy(fastOptJS in Compile)
).enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(nosketchUtil)
  .dependsOn(vonGridScalaJs)
  .dependsOn(paperScalaJs)

lazy val nosketchWebworker = (project in file("webworker"))
//  .settings(workbenchSettings: _*)
  .settings(
    scalaVersion := scalaV,
//    persistLauncher := true,
    //refreshBrowsers := refreshBrowsers.triggeredBy(fastOptJS in Compile),
//    persistLauncher in Test := false,
    resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    libraryDependencies ++= Seq(
//      "org.scala-js" %%% "scalajs-tools" % "1.0.0-M2",
//      "org.querki" %%% "querki-jsext" % "0.10"
    ),
    jsDependencies ++= Seq(
      //      RuntimeDOM,
      ProvidedJS / "bundle.js"
      //    "org.webjars" % "three.js" % "r77" / "three.js"
    ),
    scalaJSUseMainModuleInitializer := true
//    scalaJSOutputWrapper := ("", s"""canBeCloned = function(val, pathAggregator) {
//                                     var pa = (typeof pathAggregator === 'undefined') ? '' : pathAggregator;
//                                        //if (Object(val) !== val) // Primitive value return true;
//                                            switch ({}.toString.call(val).slice(8, -1)) {
//                                                // Class
//                                                case 'Boolean':
//                                                case 'Number':
//                                                case 'String':
//                                                case 'Date':
//                                                case 'RegExp':
//                                                case 'Blob':
//                                                case 'FileList':
//                                                case 'ImageData':
//                                                case 'ImageBitmap':
//                                                case 'ArrayBuffer':
//                                                    return true;
//                                                case 'Array':
//                                                case 'Object':
//                                                    return Object.keys(val).every(prop => canBeCloned(val[prop], pa + "." + prop));
//                                                case 'Map':
//                                                    return [...val.keys()].every(canBeCloned) && [...val.values()].every(canBeCloned);
//                                                case 'Set':
//                                                    return [...val.keys()].every(canBeCloned);
//                                                default:
//                                                    console.warn("this can not be serialized", pathAggregator)
//                                                    return false;
//                                            }
//                                        //}
//                                    };
//
//                                    deleteFunction = function(obj, key) {
//                                      obj[key] = undefined;
//                                    };
//
//
//                                    nosketch.worker.WorkerMain().main();
//      """),
      





    // Scala-Js Workbench (Live-Reload and such things)
//    persistLauncher in Compile := true
    //    bootSnippet := "nosketch.Viewer3D().reset();",
    //    localUrl := ("127.0.0.1", 12345),
    //    refreshBrowsers := refreshBrowsers.triggeredBy(fastOptJS in Compile)
    ).enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(nosketchUtil)

lazy val nosketchUtil = (project in file("js-util"))
  //  .settings(workbenchSettings: _*)
  .settings(
  scalaVersion := scalaV,
//  persistLauncher := true,
  //refreshBrowsers := refreshBrowsers.triggeredBy(fastOptJS in Compile),
//  persistLauncher in Test := false,
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  libraryDependencies ++= Seq(
//    "org.scala-js" %%% "scalajs-tools" % "1.0.0-M2",
    "org.querki" %%% "querki-jsext" % "0.10"
  ),
  jsDependencies ++= Seq(
    //      RuntimeDOM,
    ProvidedJS / "bundle.js"
    //    "org.webjars" % "three.js" % "r77" / "three.js"
  )
//  scalaJSOutputWrapper := ("", "nosketch.worker.WorkerMain().main();"),
  // Scala-Js Workbench (Live-Reload and such things)
//  persistLauncher in Compile := true
  //    bootSnippet := "nosketch.Viewer3D().reset();",
  //    localUrl := ("127.0.0.1", 12345),
  //    refreshBrowsers := refreshBrowsers.triggeredBy(fastOptJS in Compile)
).enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(nosketchSharedJs)
  .dependsOn(threejsFacade)




lazy val paperScalaJs = (project in file("paper-scala-js")).settings(
  scalaVersion := scalaV,
//  persistLauncher := true,
//  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "1.1.0" withJavadoc(),
//    "org.scala-js" %%% "scalajs-tools" % "1.0.0-M2" withJavadoc()
  ),
  //  jsDependencies ++= Seq(
  //    "org.webjars" % "paperjs" % "0.9.24" / "paper-full.min.js" commonJSName "paper"
  //  ),
//  persistLauncher in Compile := false,
  skip in packageJSDependencies := false
)
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)

//lazy val threejsFacade = (crossProject.crossType(CrossType.Full) in file("threejs-facade/facade"))
lazy val threejsFacade = (project in file("threejs-facade/facade"))
  .settings(
    scalaVersion := scalaV,
//    persistLauncher := true,
//    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0" withJavadoc()
      //    "org.scala-js" %%% "scalajs-tools" % "1.0.0-M2" withJavadoc()
    ),
    //  jsDependencies ++= Seq(
    //    "org.webjars" % "paperjs" % "0.9.24" / "paper-full.min.js" commonJSName "paper"
    //  ),
//    persistLauncher in Compile := false,
    skip in packageJSDependencies := false
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)

val bundle = project.in(file("bundle"))

addCommandAlias("bundle", "bundle/bundle")


lazy val nosketchShared = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := scalaV
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val nosketchSharedJvm = nosketchShared.jvm/* dependsOn threejsFacade*/
lazy val nosketchSharedJs = nosketchShared.js.dependsOn(threejsFacade)


//lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"




skip in packageJSDependencies := false



// Resolve only newly added dependencies
updateOptions := updateOptions.value.withCachedResolution(true)

//updateBrowsers := updateBrowsers.triggeredBy(fastOptJS in Compile)


//
//scalaJSStage in Global := FastOptStage

//persistLauncher in Compile := false

//persistLauncher in Test := false

skip in packageJSDependencies := false

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project nosketchJVM", _: State)) compose (onLoad in Global).value


