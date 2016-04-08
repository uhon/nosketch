package nosketch

import java.awt.event.MouseWheelEvent

import components._
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{FPSIndicator, MouseIndicator, TextIndicator, TouchIndicator}
import nosketch.util.Profiler._
import nosketch.viewport.ViewPort
import org.denigma.threejs.extensions.controls.CameraControls
import org.denigma.threejs.extras.HtmlSprite
import org.denigma.threejs._
import org.denigma.threejs.extensions.Container3D
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import paperjs.Paper._

import scala.collection.mutable
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.{Paper, PaperScope}

import scala.scalajs.js
import scala.scalajs.js.Any
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._
import org.querki.jquery._
import vongrid._
import vongrid.lib._
import vongrid.config._
import vongrid.utils.{MC, MouseCaster, Scene}
import scala.scalajs.js.timers._

import js.Dynamic.{global => g}
import js.Dynamic.{literal => l}
import scala.util.Random

@JSExport
object Viewer3D extends scala.scalajs.js.JSApp with ViewportSubscriber {

  var viewPort: ViewPort = null
  var grid: NSGrid = null
  var board: NSBoard = null
  var scene: Scene = null

//  var clusterList: List[Cluster] = List()


  /**
   * Main Entry-Point when not triggered by Script-Tag in Html-Head
   */
  @JSExport
  def main() = {
    $("document").ready(() => startViewer(document.getElementById("magicContainer")) )

  }

  /**
   * starts the Viewer by creating and initalizing ViewPort, initHexagons, adding HUD and rezising the View (to the window size)
    *
    * @param element
   */
  @JSExport
  def startViewer(element: Element): Unit = {
    console.log("starting viewer")
    //if(canvas == null) { return }



    //console.log("created viewport")
    // Initialize the ViewPort

    activate(element)

    //viewPort = new ViewPort($("#magicContainer canvas").get(0).asInstanceOf[dom.html.Canvas], this)
    //viewPort.init

  }

  var lastFrustum: Long = System.currentTimeMillis()

  def activate(element: Element): Unit = {
    console.log("activate")



    element.asInstanceOf[HTMLElement].style.backgroundColor = "#DDFFDD"
    scene = new Scene(
      l(
        "element" -> element,
        "cameraPosition" -> new Vector3(0, 150, 150)
        //"fog" -> new Fog(0x003300, 340, 370)
      ).asInstanceOf[SceneConfig],
      true
    )

    scene.render()

    // this constructs the cells in grid coordinate space
    grid = new NSGrid(/*l(
        "cellSize" -> 11,
        "cameraPosition" -> new Vector3(0, 0, 150),
        "fog" -> new Fog(0xFFFFFF, 200, 400)
      ).asInstanceOf[HexGridConfig]*/
    )

    //grid.generate(l("size" -> 5).asInstanceOf[SimpleTileGenConfig])


    val mouse = new NSMouseCaster(scene.container, scene.camera);
    board = new NSBoard(grid, js.undefined)

    // this will generate extruded hexagonal tiles
    board.generateTilemap(l(
        "tileScale" -> 0.97f // you might have to scale the tile so the extruded geometry fits the cell size perfectly
      ).asInstanceOf[TileGenConfig]
    )


    initHexagons

    scene.add(board.group)
    scene.focusOn(board.group)

    mouse.signal.add((evt: String, tile: js.Object) => {
      if (evt == MC.OVER) {
        if(System.currentTimeMillis() - lastFrustum > 10) {
          frustum.setFromMatrix( new Matrix4().multiplyMatrices( scene.camera.projectionMatrix, scene.camera.matrixWorldInverse ) )
          updateView(true)
          lastFrustum = System.currentTimeMillis()
        }

      }
      if (evt == MC.WHEEL) {
        if(System.currentTimeMillis() - lastFrustum > 10) {
          frustum.setFromMatrix( new Matrix4().multiplyMatrices( scene.camera.projectionMatrix, scene.camera.matrixWorldInverse ) )
          updateView(true)
          lastFrustum = System.currentTimeMillis()
        }

      }
      if (evt == MC.CLICK) {

        // tile.toggle();
        // or we can use the mouse's raw coordinates to access the cell directly, just for fun:
        var cell = board.getGrid.getCellAt(mouse.position)
        val visualHex = cell.asInstanceOf[VisibleHexagon]

        if(cell.isDefined) {
          val t = board.getTileAtCell(cell.get)
          if (t.isDefined) {
            t.get.toggle()
            console.log("cell:", cell)
            console.log("neighbours", cell.asInstanceOf[VisibleHexagon].neighbours)
            cell.asInstanceOf[VisibleHexagon].neighbours.zipWithIndex.foreach {
              case (c: VisibleHexagon, i: Int) => setTimeout(i*100) { board.getTileAtCell(c).toOption.get.toggle() }
            }

          }
          cell
        }
      }
    }, this.asInstanceOf[js.Object])


//    mouse.signal.add(function(evt, tile) {
//      if (evt === vg.MouseCaster.CLICK) {
//        // tile.toggle();
//        // or we can use the mouse's raw coordinates to access the cell directly, just for fun:
//        var cell = board.grid.pixelToCell(mouse.position);
//        var t = board.getTileAtCell(cell);
//        if (t) t.toggle();
//      }
//    }, this);

    update

    def update {
      //console.log("repaint")
        mouse.update
        scene.render
        g.requestAnimationFrame(() => update)
    }
  }




  def initHexagons = {
    val startTime = System.nanoTime
//
//    grid.generate(l("size" -> 0).asInstanceOf[SimpleTileGenConfig])

   val initialHex = new ImageHexagon(grid)
    //grid.generateTiles
    grid.add(initialHex)
    grid.generateTile(initialHex, 0.97d)
    grid.getVisibleCells.foreach(_._2.assignNeighbours)
    //reportDuration("Viewer::initHexagons", startTime)
    updateView()
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
   * if one was found, it gets retruned, otherwise a new Hexagon is created and drawn immediately
    *
    * @param center the center position of the new Hexagon
   * @return
   */
  def findOrCreateHexagon(center: Vector3): (Cell, Boolean) = {
//    console.log("find or create hexagon at position", center)
    val startTime = System.nanoTime
    try {
      if (isOutOfScope(center)) {
        //console.log("Not creating Hexagon at Pos", center.toString())
        return (PhantomHexagon, false)
      }


//      console.log("Find cell at", center, grid.getCellAt(center).getOrElse("no cell found"))
      grid.getCellAt(center).toOption match {
        case Some(c: Cell) => console.log("found hexagon"); (c.asInstanceOf[VisibleHexagon], false)
        case None => {
          // TODO: call cluster logic from here to create hexagons with sketches
          // for now just test-shapes and Hexagons Types shown
          console.log("No hexagon present, draw a new")
          val tmpCell = grid.pixelToCell(center)
          val newHex = new ImageHexagon(grid, tmpCell.q, tmpCell.r, tmpCell.s, 1)
          grid.add(newHex)

          setTimeout(400 * Math.random()) { board.addTile(grid.generateTile(newHex, 0.97d)) }
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
    * @param forceRedrawVisible
   */
  def updateView(forceRedrawVisible: Boolean = false) = {
    console.log("updating view...")
    val startTime = System.nanoTime
    //console.log("update viewPort with Bounds:" + viewPort.getView.bounds.right, viewPort.getView.bounds.top, viewPort.getView.bounds.left, viewPort.getView.bounds.bottom)
    //console.log("SIZE OF VISIBLE HEXAGONS: ", visibleHexagons.size, printCoordinates(visibleHexagons))


    console.log("visible Hexagons #", grid.getVisibleCells.size)
    // Remove invisible hexagons
    grid.getVisibleCells.foreach((t: Tuple2[String,VisibleHexagon]) => {
      console.log("check if out of scope", t._2.getCenter)
      if(isOutOfScope(t._2.getCenter)) {
        console.log("became out of scope")
        board.removeTile(t._2.tile)
        t._2.destroy
        grid.remove(t._2)
      }
    })
    reportDuration("Viewer::remove invisible", startTime)
    console.log("visible Hexagons after cleanup #", grid.getVisibleCells.size)

    // Assign new hexagons and redraw them when appear
    val assignNeighboursTime = System.nanoTime


    grid.getVisibleCells.foreach(_._2.assignNeighbours)


    console.log("visible Hexagons after expanding #", grid.getVisibleCells.size)
    reportDuration("Viewer::remove invisible", assignNeighboursTime)

    //scene.render()
//    if(forceRedrawVisible) {
//      console.log("forced redraw of all hexagons")
//      grid.cells.foreach(_._2.redraw(viewPort.scaleFactor))
//    }
    // We don't redraw all Hexagons here (meight not be necessary)
    reportDuration("Viewer::updateView", startTime)
    //scene.render()
  }

  override def onZoom = {
    if (viewPort != null) {
      //DebugHUD.redraw(viewPort)
      updateView()
    }
  }

  override def onScale = {
    if (viewPort != null) {
      //DebugHUD.redraw(viewPort)
      //this.clusterList.foreach(_.redraw(viewPort.scaleFactor))
      updateView(forceRedrawVisible = true)

    }
  }

}
