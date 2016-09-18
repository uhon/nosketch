package nosketch

import java.awt.event.MouseWheelEvent

import components._
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{MouseIndicator, TextIndicator, TouchIndicator}
import nosketch.io.{ImageUrls, NSSprite}
import nosketch.loading.NSTextureLoader
import nosketch.shared.util.{FA, GridConstants}
import nosketch.shared.util.FA.FA
import nosketch.util.Profiler._
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
import scala.scalajs.js.Any
import scala.scalajs.js.annotation.{JSExport, JSName}
import org.scalajs.dom._
import org.querki.jquery._
import vongrid._
import vongrid.lib._
import vongrid.config._
import org.denigma.threejs._
import vongrid.controls.{Mouse, MouseButtons, OrbitControlsPort}
import vongrid.utils.{MC, MouseCaster, Scene}

import scala.scalajs.js.timers._
import js.Dynamic.{global => g}
import js.Dynamic.{literal => l}
import scala.util.Random

@JSExport
@JSName("nosketch.Viewer3D")
object Viewer3D extends scala.scalajs.js.JSApp with ViewportSubscriber {

  var viewPort: ViewPort = null
  var grid: NSGrid = null
  var board: NSBoard = null
  var scene: Scene = null
  var mouseAtTile: Option[NSTile] = None
  var predefinedTextures: Map[FA, Texture] = Map()
  var mouse: MouseCaster = null

//  var clusterList: List[Cluster] = List()


  /**
   * Main Entry-Point when not triggered by Script-Tag in Html-Head
   */
  @JSExport
  def main() = {
    // TODO: Enable again if run without workbench
    $("document").ready(() => startViewer(document.getElementById("magicContainer")) )
  }

  @JSExport
  @JSName("reset")
  def reset() = {
    console.log("reseting and restarting  CURENTLY DEACTIVATED")
//    if(grid != null) {
//      grid.getVisibleCells.foreach((t: Tuple2[String,VisibleHexagon]) => t._2.destroy)
//    }
//    startViewer(document.getElementById("magicContainer"))
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



    //console.log("created viewport")
    // Initialize the ViewPort

    activate(element)

    //viewPort = new ViewPort($("#magicContainer canvas").get(0).asInstanceOf[dom.html.Canvas], this)
    //viewPort.init

  }


  def activate(element: Element): Unit = {
    console.log("activate")

    scene = new Scene(
      SceneConfig.element(element.asInstanceOf[HTMLElement])
        .cameraPosition(new Vector3(0, 70, -70))
        .fog(new Fog(0x000000, 300, 400)),
      false
    )

    object mouseButtons extends MouseButtons {
      ORBIT = Mouse.RIGHT
      PAN = Mouse.LEFT
    }

    scene.controls = new OrbitControlsPort(scene.camera, scene.renderer.domElement.asInstanceOf[HTMLElement], mouseButtons)

//    scene.controls.mouseButtons = l(ORBIT = 2, ZOOM = 1, PAN = 0)
//    scene.controls.keys = l(LEFT = 37, UP = 38, RIGHT = 39, BOTTOM = 40)
    scene.controls.enableKeys = true
    scene.controls.enableDamping = true
    scene.controls.enableRotate = true
    scene.controls.enableZoom = true
    scene.controls.zoomSpeed = 2

//        scene.controls.autoRotate = true
//        scene.controls.autoRotateSpeed = 20
    scene.controls.minDistance = 30
    scene.controls.maxDistance = 350


//    scene.controls.maxAzimuthAngle = Math.PI/2
//    scene.controls.minAzimuthAngle = -Math.PI/2
//
    scene.controlled = false

//    console.log("controls", scene.controls)





    // this constructs the cells in grid coordinate space
    grid = new NSGrid(/*l(
        "cellSize" -> 11,
        "cameraPosition" -> new Vector3(0, 0, 150),
        "fog" -> new Fog(0xFFFFFF, 200, 400)
      ).asInstanceOf[HexGridConfig]*/
    )

    //grid.generate(l("size" -> 5).asInstanceOf[SimpleTileGenConfig])


    mouse = new NSMouseCaster(scene.container, scene.camera)


    board = new NSBoard(grid, js.undefined)

    // this will generate extruded hexagonal tiles
    board.generateTilemap(TileGenConfig.tileScale(0.97f))




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
    DebugHUD.addElement(new TextIndicator("number of visible Hexagons", () => grid.getVisibleCells.size.toString))
    DebugHUD.addElement(new TextIndicator("mousePosition", () => s"${Math.round(mouse.position.x * 100) / 100},${Math.round(mouse.position.y * 100) / 100},${Math.round(mouse.position.z * 100) / 100}"))

    NSTextureLoader.loadFA((map: Map[FA, Texture]) => {

      predefinedTextures = map
      scene.render()
      createFirstHexagon
      update
    })


  }


  def showControls(evt: String, obj: js.Object) = this.synchronized {
    val previousTile = mouseAtTile
    val currentTile = obj match {
      case t:NSTile => Option(t)
      case s:NSSprite => Option(s.tile.asInstanceOf[NSTile])
      case _ => previousTile
    }

    (currentTile, previousTile) match {
      case (Some(c), Some(p)) if c.uniqueID != p.uniqueID => {
        c.showControls
        p.hideControls
      }
      case (Some(c), None) => c.showControls
      case (None, Some(p)) => p.hideControls
      case _ =>
    }
    mouseAtTile = currentTile
  }

  def initMouseEvents: Unit = {
    mouse.signal.add((evt: String, tile: js.Object) => {
      if (evt == MC.OVER) {
        showControls(evt, tile)
//        requestViewUpdate
      }

      if (evt == MC.OUT) {
//        requestViewUpdate
      }

      if (evt == MC.WHEEL) {
//        requestViewUpdate
      }

      if (evt == MC.CLICK) {

        // tile.toggle();
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


  def update {

    mouse.update
    DebugHUD.update
    scene.controls.update.apply()
    if(updateRequested && !updateInProgress) {
      updateInProgress = true
      updateRequested = false
      frustum.setFromMatrix( new Matrix4().multiplyMatrices( scene.camera.projectionMatrix, scene.camera.matrixWorldInverse ) )

      updateView

      scene.render
      updateInProgress = false
      //console.log("repaint")
      //
    }


    g.requestAnimationFrame(() => update)

  }




  def createFirstHexagon = {
    val startTime = System.nanoTime
//
//    grid.generate(l("size" -> 0).asInstanceOf[SimpleTileGenConfig])

   val initialHex = new ImageHexagon(grid)
    //grid.generateTiles
    grid.add(initialHex)
    val  tile = grid.generateNSTile(initialHex, GridConstants.tileScaleFactor)
    board.addTile(tile)
//    initialHex.draw
    tile.selected
    //grid.getVisibleCells.foreach(_._2.assignNeighbours)
    //reportDuration("Viewer::initHexagons", startTime)
//    requestViewUpdate
    //setTimeout(() => updateView(), 2000)
  }

  val frustum: Frustum = new Frustum()

  def isOutOfScope(pos: Vector3) = {


//    console.log("distance to cammera", scene.camera.position.distanceTo(pos) )
    /* || */

    scene.camera.position.distanceTo(pos) > 400 || !frustum.intersectsSphere(new Sphere(pos, 11))

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
    val startTime = System.nanoTime
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
          val newHex = new ImageHexagon(grid, tmpCell.q, tmpCell.r, tmpCell.s, GridConstants.tileInitialHeight)


//          setTimeout(400 * Math.random()) {
          val newTile = grid.generateNSTile(newHex, GridConstants.tileScaleFactor)
          newHex.draw
          board.group.add(newTile.sprites)
          grid.add(newHex)
          board.addTile(newTile)

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
    } finally reportDuration("Viewer::findOrCreateHexagon", startTime)
  }


  def printCoordinates(visibleHexagons: mutable.Map[Point, VisibleHexagon]): String = {
    visibleHexagons.map(vH => s"""[${vH._1.x}:${vH._1.y}]""").mkString(",")
  }

  /**
   * Updates the view and redraws all Hexagon which will newly appear on screen
    *
   */
  def updateView = {
    DebugHUD.updateView.increment
//    console.log("updating view...")
    val startTime = System.nanoTime
    //console.log("update viewPort with Bounds:" + viewPort.getView.bounds.right, viewPort.getView.bounds.top, viewPort.getView.bounds.left, viewPort.getView.bounds.bottom)
    //console.log("SIZE OF VISIBLE HEXAGONS: ", visibleHexagons.size, printCoordinates(visibleHexagons))




//    console.log("visible Hexagons #", grid.getVisibleCells.size)
    // Remove invisible hexagons
    grid.getVisibleCells.foreach((t: Tuple2[String,VisibleHexagon]) => {
      //console.log(t._2)
//      console.log("check if out of scope", t._2.getCenter)
      if(isOutOfScope(t._2.getCenter)) {
        //console.log("became out of scope")
        t._2.getTile match {
          case t: NSTile => t.map(board.removeTile(_))
          case _ =>
        }
        t._2.destroy
        grid.remove(t._2)
      }
    })
    reportDuration("Viewer::remove invisible", startTime)

    // Assign new hexagons and redraw them when appear
    val assignNeighboursTime = System.nanoTime


    grid.getVisibleCells.foreach(_._2.assignNeighbours)

    reportDuration("Viewer::assign Neighbours", assignNeighboursTime)
  }


  var updateRequested = false
  var updateInProgress = false

  def requestViewUpdate: Unit = {
    updateRequested = true
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
