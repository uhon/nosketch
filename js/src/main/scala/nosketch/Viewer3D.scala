package nosketch

import java.awt.event.MouseWheelEvent

import components._
import nosketch.Config.{Camera, Grid, Scene}
import nosketch.controls.camera.NSOrbitControls
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{MouseIndicator, TextIndicator, TouchIndicator}
import nosketch.io.NSSprite
import nosketch.util.loading.NSTextureLoader
import nosketch.provider.{MeshProvider, ShapeTextureProvider}
import nosketch.util.loading.FA
import nosketch.util.loading.FA.FA
import nosketch.util.Profiler._
import nosketch.util.io.ImageUrls
import nosketch.viewport.ViewPort
import org.denigma.threejs.{Texture, _}
import org.denigma.threejs.extras.{HtmlSprite, OrbitControls}
import org.denigma.threejs.extensions.Container3D
import org.scalajs.dom.raw.HTMLElement
import paperjs.Paper._

import scala.collection.mutable
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.{Paper, PaperScope}

import scala.scalajs.js
import scala.scalajs.js.{Any, JSApp, JSON}
import scala.scalajs.js.annotation.{JSExport, JSName}
import org.scalajs.dom._
import org.querki.jquery._
import vongrid._
import vongrid.lib._
import vongrid.config._
import org.denigma.threejs._
import vongrid.controls.{Mouse, MouseControls, OrbitControlsPort}
import vongrid.utils.{MC, MouseCaster, Scene}

import scala.concurrent.duration._
import scala.scalajs.js.timers._
import js.Dynamic.{global => g}
import js.Dynamic.{literal => l}
import scala.language.postfixOps
import scala.util.Random



@JSExport
@JSName("nosketch.Viewer3D")
object Viewer3D extends JSApp with ViewportSubscriber {

  var viewPort: ViewPort = null
  var grid: NSGrid = null
  var board: NSBoard = null
  var scene: Scene = null
  var mouseAtTile: Option[NSTile] = None
  var predefinedTextures: Map[FA, Texture] = Map()
  var mouse: MouseCaster = null

  var stats: Stats = null



//  var clusterList: List[Cluster] = List()


  /**
   * Main Entry-Point when not triggered by Script-Tag in Html-Head
   */
  @JSExport
  def main() = {
    // TODO: Enable again if run without workbench
    $("document").ready(() => startViewer(document.getElementById("magicContainer")) )
//    reset
  }

  var resetInProgress = false

  @JSExport
  @JSName("reset")
  def reset() = {
    if(resetInProgress) {
      console.log("reset avoided its in progress")
    } else {
      resetInProgress = true
      console.log("reseting and restarting")
      if (grid != null) {
        grid.getVisibleCells.foreach((t: Tuple2[String, VisibleHexagon]) => t._2.destroy)

        //      scene = null
        //      grid.dispose()
        //      mouse = null
        //      board

      }
      activate(document.getElementById("magicContainer")).apply
      //    startViewer(document.getElementById("magicContainer"))
      //    DebugHUD.reset
      //    scene.render()
      //    createFirstHexagon
      //    requestViewUpdate
      //    update
      resetInProgress = false
    }
  }

  /**
   * starts the Viewer by creating and initalizing ViewPort, initHexagons, adding HUD and rezising the View (to the window size)
    *
    * @param element
   */
  @JSExport
  def startViewer(element: Element): Unit = {
    console.log("starting the viewer")
    //if(canvas == null) { return }


//
//    NSTextureLoader.load(ImageUrls.randomPngShape, (t: Texture) => {
//      println("loaded texture convert it to JSON")
//      console.log("loaded texture convert it to JSON")
//      val jsonString = JSON.stringify(t.toJSON().asInstanceOf[aResult])
//      console.log(jsonString)
//
//      var loader = new JSONLoader()
//
//      var tex = loader.parse( jsonString )
//
//
//      console.log("tex", tex)
////      console.log(JSON.stringify(t))
//    })


    //console.log("created viewport")
    // Initialize the ViewPort

    var takeOff = activate(element)
    preload(takeOff)

    //viewPort = new ViewPort($("#magicContainer canvas").get(0).asInstanceOf[dom.html.Canvas], this)
    //viewPort.init

  }


  def activate(element: Element): () => Unit = {
    console.log("activate")


    MeshProvider.init
    MeshProvider.startCaching()



    if(scene == null) {
      console.log("creating scene")
      scene = new Scene(
        SceneConfig.element(element.asInstanceOf[HTMLElement])
          .antialias(true)
          .cameraPosition(Config.Camera.initialCameraPos)
          .fog(new Fog(Config.Scene.fogColor, Config.World.distanceClipping - Config.World.fogDepth , Config.World.distanceClipping)),
        false
      )

      val camera = scene.camera.asInstanceOf[OrthographicCamera]
      camera.far = Config.World.distanceClipping
      camera.updateProjectionMatrix()
      console.log("scene camera far", camera.far)

      scene.controls = new NSOrbitControls(scene.camera, scene.renderer.domElement.asInstanceOf[HTMLElement])
      Config.OrbitControls.apply(scene.controls.asInstanceOf[NSOrbitControls])
      scene.controls.addEventListener("change", (a: js.Any) => requestViewUpdate)
      scene.controls.addEventListener("start", (a: js.Any) => requestViewUpdate)
      scene.controls.addEventListener("end", (a: js.Any) => requestViewUpdate)

      scene.controlled = false
    }


    if(grid == null) {
      console.log("creating grid")
      // this constructs the cells in grid coordinate space
      grid = new NSGrid()
    }

    //grid.generate(l("size" -> 5).asInstanceOf[SimpleTileGenConfig])

    if(mouse == null) {
      console.log("creating Mousecaster")
      mouse = new NSMouseCaster(scene.container, scene.camera)
    }


    if(board == null) {
      console.log("creating board and tilemap")
      board = new NSBoard(grid, js.undefined)
      // this will generate extruded hexagonal tiles
      board.generateTilemap(TileGenConfig.tileScale(0.97f))
    }






    scene.add(board.group)
    scene.focusOn(board.group)

    initMouseEvents





    //    mouse.signal.add(function(evt, tile) {
    //      if (evt === vg.MouseCaster.CLICK) {
    //        // tile.toggle();
    //        // or we can use the mouse's raw coordinates to access the cell directly, just for fun:
    //        var cell = board.grid.pixelToCell(mouse.position);
    //        var t = board.getTileAtCell(cell);
    //        if (t) t.toggle();
    //      }
    //    },
    DebugHUD.addElement(new TextIndicator("# visible Hex", () => grid.getVisibleCells.size))
    DebugHUD.addElement(new TextIndicator("° mouse position", () => new Vector3(mouse.position.x, mouse.position.y, mouse.position.z)))

    stats = new Stats()
    stats.showPanel(0); // 0: fps, 1: ms, 2: mb, 3+: custom
    $("body").append(stats.dom)

    () => {
        scene.render()
        createFirstHexagon
        requestViewUpdate
        update
      }
  }

  // FIXME: We don't want to do that. remove it
  def preload(takeOff: () => Unit) = {
    NSTextureLoader.loadFA((map: Map[FA, Texture]) => {
      console.log("textures loaded")
      predefinedTextures = map
      takeOff.apply
    })
  }


  def showControls(evt: String, obj: js.Object) = this.synchronized {
//    setTimeout(100 milliseconds) {
      val previousTile = mouseAtTile


      val currentTile = obj match {
        case t:NSTile => Option(t)
        case s:NSSprite => Option(s.tile.asInstanceOf[NSTile])
        case _ => previousTile
      }

      (currentTile, previousTile) match {
        case (Some(c), Some(p)) if c.uniqueID != p.uniqueID => {
          p.hideControls
          c.showControls
        }
        case (Some(c), None) => c.showControls
        case (None, Some(p)) => p.hideControls
        case _ =>
      }
      mouseAtTile = currentTile
//    }
  }

  def initMouseEvents: Unit = {
    mouse.signal.add((evt: String, tile: js.Object) => {
      if (evt == MC.OVER) {
        showControls(evt, tile)
        requestSceneUpdate
      }

      if (evt == MC.OUT) {
//        requestViewUpdate
      }

      if (evt == MC.WHEEL) {
//        requestViewUpdate
      }

      if (evt == MC.CLICK) {
        // or we can use the mouse's raw coordinates to access the cell directly, just for fun:
        var cell = board.getGrid.getCellAt(mouse.position)
        //        console.log("mouse.position", mouse.position)
        //        console.log("cell", cell)

        if(cell.isDefined) {
          val t = board.getTileAtCell(cell.get)
          if (t.isDefined) {
            //t.get.toggle()

            //            console.log("cell:", cell)
            //            console.log("neighbours", cell.asInstanceOf[VisibleHexagon].neighbours)
            cell.asInstanceOf[VisibleHexagon].neighbours.zipWithIndex.foreach {
              case (c: VisibleHexagon, i: Int) => setTimeout(i*100) { board.getTileAtCell(c).toOption.get.toggle() }
            }

          }
          cell
        }
      }
    }, this.asInstanceOf[js.Object])
  }



  var numberOfupdates = 0
  def update {
    numberOfupdates += 1
    stats.begin()
    mouse.update

    DebugHUD.cameraPosition.setValue(scene.camera.position)
    DebugHUD.cameraRotation.setValue(scene.camera.rotation)

    DebugHUD.update
    scene.controls.update.apply()


    MeshProvider.enableServing




    MeshProvider.serveRequesters
    grid.getVisibleCells.foreach((t: Tuple2[String,VisibleHexagon]) => {
      t._2.animate
    })



    if(sceneUpdateRequested) {
      DebugHUD.sceneUpdates.increment
      scene.render
      sceneUpdateRequested = false
    }

    if(viewUpdateRequested && !viewUpdateInProgress) {
      viewUpdateInProgress = true
      viewUpdateRequested = false

      frustum.setFromMatrix( new Matrix4().multiplyMatrices( scene.camera.projectionMatrix, scene.camera.matrixWorldInverse ) )


        updateView

        viewUpdateInProgress = false
    }

    stats.end()
    g.requestAnimationFrame(() => update)

  }




  def createFirstHexagon = {
    val startTime = System.currentTimeMillis()
//
//    grid.generate(l("size" -> 0).asInstanceOf[SimpleTileGenConfig])

   val initialHex = new ImageHexagon(grid)


    val tile = initialHex.draw
    //grid.generateTiles
    grid.add(initialHex)
    tile.selected
  }

  val frustum: Frustum = new Frustum()

  def isOutOfScope(pos: Vector3) = {


//    console.log("distance to cammera", scene.camera.position.distanceTo(pos) )
    /* || */
//    scene.camera.position.distanceTo(pos) > Config.world.radius || !frustum.intersectsSphere(new Sphere(pos, Config.world.intersectionSphereRadius))

    val cp = scene.camera.position
      val posOnSameY = new Vector3(cp.x, 0, cp.z)
      !frustum.intersectsSphere(new Sphere(pos, Config.World.intersectionSphereRadius))
      // if you ever need a small one
//    scene.camera.position.distanceTo(pos) > 100 || !frustum.intersectsSphere(new Sphere(pos, 3))

    //    false


  }


  /**
   * Searches for Hexagons among the visibleHexagons
   * if one was found, it gets returned, otherwise a new Hexagon is created and drawn immediately
    *
    * @param center the center position of the new Hexagon
   * @return
   */
  def findOrCreateHexagon(center: Vector3): (Cell, Boolean) = {

//    console.log("find or create hexagon at position", center)
    val startTime = System.currentTimeMillis()
    try {
      val possibleCell = grid.getCellAt(center).toOption

      if (isOutOfScope(center)) {
        //console.log("Not creating Hexagon at Pos", center.toString())
        possibleCell.map((c) => c.asInstanceOf[VisibleHexagon].destroy)
        return (PhantomHexagon, false)
      }

//      console.log("Find cell at", center, grid.getCellAt(center).getOrElse("no cell found"))
      possibleCell match {
        case Some(c: VisibleHexagon) => (c, false)
        case None => {
          // TODO: call cluster logic from here to create hexagons with sketches
          // for now just test-shapes and Hexagons Types shown
//          console.log("No hexagon present, draw a new")
          val tmpCell = grid.pixelToCell(center)
          val newHex = new ImageHexagon(grid, tmpCell.q, tmpCell.r, tmpCell.s, Grid.tileInitialHeight)


//          setTimeout(400 * Math.random()) {
          grid.add(newHex)
          newHex.draw

          //          board.generateDroppingTile(newHex)


//          }
          //setTimeout(10) { newHex.assignNeighbours }

//            } else if((Math.random() * 5).toInt % 4 == 0) {
//              newHex = new EmptyHexagon(center, radius, scaleFactor)
//            } else {
//              newHex = new Hexagon(center, radius, scaleFactor)
//              newHex.asInstanceOf[Hexagon].addScratchShapes
//            }


          (newHex, true)
        }
      }
    } finally reportDuration("Δ findOrCreate", startTime)
  }


  def printCoordinates(visibleHexagons: mutable.Map[Point, VisibleHexagon]): String = {
    visibleHexagons.map(vH => s"""[${vH._1.x}:${vH._1.y}]""").mkString(",")
  }

  /**
   * Updates the view and redraws all Hexagon which will newly appear on screen
    *
   */
  def updateView = {
    DebugHUD.viewUpdates.increment

//    console.log("updating view...")
    val startTime = System.currentTimeMillis()
    //console.log("update viewPort with Bounds:" + viewPort.getView.bounds.right, viewPort.getView.bounds.top, viewPort.getView.bounds.left, viewPort.getView.bounds.bottom)
    //console.log("SIZE OF VISIBLE HEXAGONS: ", visibleHexagons.size, printCoordinates(visibleHexagons))




//    console.log("visible Hexagons #", grid.getVisibleCells.size)
    // Remove invisible hexagons
    grid.getVisibleCells
      .filter(c => c._2.neighbours.contains(PhantomHexagon))
      .foreach((t: Tuple2[String,VisibleHexagon]) => {
      //console.log(t._2)
//      console.log("check if out of scope", t._2.getCenter)
      if(isOutOfScope(t._2.getCenter)) {
        //console.log("became out of scope")
        t._2.getTile.map(board.removeTile(_))
        t._2.destroy
        grid.remove(t._2)
      }
    })
    reportDuration("Δ rem. invisible", startTime)

    // Assign new hexagons and redraw them when appear
    val assignNeighboursTime = System.currentTimeMillis()


    grid.getVisibleCells.foreach(_._2.assignNeighbours)

    reportDuration("Δ assign Neighb.", assignNeighboursTime)
    requestSceneUpdate
//
//    // apply Shapes to tiles
//    ShapeGeometryProvider.serveRequesters
  }


  var viewUpdateRequested = false
  var viewUpdateInProgress = false
  var sceneUpdateRequested = false

  def requestViewUpdate: Unit = {
    viewUpdateRequested = true
  }

  def requestSceneUpdate: Unit = {
    sceneUpdateRequested = true
  }

  override def onZoom = {

    if (viewPort != null) {
      //DebugHUD.redraw(viewPort)
      requestViewUpdate
    }
  }

  override def onScale = {
    if (viewPort != null) {
      //DebugHUD.redraw(viewPort)
      //this.clusterList.foreach(_.redraw(viewPort.scaleFactor))
      requestViewUpdate

    }
  }

}

trait aResult extends js.Object {
  val image: js.Object
}
