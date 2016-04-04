package nosketch

import java.awt.event.MouseWheelEvent

import nosketch.components._
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

import js.Dynamic.{global => g}
import js.Dynamic.{literal => l}
import scala.util.Random

@JSExport
object Viewer3D extends scala.scalajs.js.JSApp with ViewportSubscriber {

  var visibleHexagons: mutable.Map[Point, VisibleHexagon] = mutable.Map()

  var viewPort: ViewPort = null

  var clusterList: List[Cluster] = List()


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

  def activate(element: Element): Unit = {
    console.log("activate")



    element.asInstanceOf[HTMLElement].style.backgroundColor = "green"
    val scene = new Scene(
      l(
        "element" -> element,
        "cameraPosition" -> new Vector3(0, 150, 150),
        "fog" -> new Fog(0xFFFFFF, 200, 400)
      ).asInstanceOf[SceneConfig],
      true)

    scene.render()

    console.log("print properties")
    console.log(scene)

    // this constructs the cells in grid coordinate space
    val grid = new HexGrid(l(
        "cellSize" -> 11,
        "cameraPosition" -> new Vector3(0, 0, 150),
        "fog" -> new Fog(0xFFFFFF, 200, 400)
      ).asInstanceOf[HexGridConfig]
    )

    grid.generate(l("size" -> 5).asInstanceOf[SimpleTileGenConfig])

    val mouse = new MouseCaster(scene.container, scene.camera);
    var board = new Board(grid)

    // this will generate extruded hexagonal tiles
    board.generateTilemap(l(
        "tileScale" -> 0.91f // you might have to scale the tile so the extruded geometry fits the cell size perfectly
      ).asInstanceOf[TileGenConfig]
    )



    scene.add(board.group)
    scene.focusOn(board.group)

    mouse.signal.add((evt: String, tile: js.Object) => {
      if (evt == MC.CLICK) {
        // tile.toggle();
        // or we can use the mouse's raw coordinates to access the cell directly, just for fun:
        var cell = board.grid.pixelToCell(mouse.position)
        if(cell.isDefined) {
          val t = board.getTileAtCell(cell.get)
          if (t.isDefined) t.get.toggle();
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
    val initialHex = new ImageHexagon(viewPort.center, view.size.width / 10, 1)
    visibleHexagons.put(viewPort.center.round(), initialHex)
    initialHex.assignNeighbours
    //reportDuration("Viewer::initHexagons", startTime)
    updateView()
    //setTimeout(() => updateView(), 2000)
  }

  def isOutOfScope(center: Point, radius: Double) = {
    val startTime = System.nanoTime
    val viewportBounds = viewPort.getView.bounds

    val distE = (viewportBounds.right * viewPort.scaleFactor - center.x * viewPort.scaleFactor) +radius / 3 * viewPort.scaleFactor
    val distN = (viewportBounds.top * viewPort.scaleFactor - center.y * viewPort.scaleFactor ) -radius / 3 * viewPort.scaleFactor
    val distW = (viewportBounds.left * viewPort.scaleFactor - center.x * viewPort.scaleFactor ) -radius / 3 * viewPort.scaleFactor
    val distS = (viewportBounds.bottom * viewPort.scaleFactor - center.y * viewPort.scaleFactor ) +radius / 3 * viewPort.scaleFactor


    val result = (distE < 0 && Math.abs(distE) > radius) ||
    (distN > 0 && Math.abs(distN) > radius) ||
    (distW > 0 && Math.abs(distW) > radius) ||
    (distS < 0 && Math.abs(distS) > radius)

    //reportDuration("Viewer::isOutOfScope", startTime)
    result
  }


  /**
   * Searches for Hexagons among the visibleHexagons map by key as roundedCenter
   * if one was found, it gets retruned, otherwise a new Hexagon is created and drawn immediately
    *
    * @param center the center position of the new Hexagon
   * @param radius radius of the new Hexagon
   * @param scaleFactor scale Factor to Scale the whole thing
   * @return
   */
  def findOrCreateHexagon(center: Point, radius: Double, scaleFactor: Double): (AbstractHexagon, Boolean) = {
    //console.log("find or create hexagons")
    val startTime = System.nanoTime
    try {
      if (Viewer.isOutOfScope(center, radius)) {
        //console.log("Not creating Hexagon at Pos", center.toString())
        return (PhantomHexagon, false)
      }

      val roundedCenter = center.round()
      visibleHexagons.find(p => p._1.equals(roundedCenter)) match {
        case Some(x) => (x._2, false)
        case None => {
          // TODO: call cluster logic from here to create hexagons with sketches
          // for now just test-shapes and Hexagons Types shown
          var newHex:VisibleHexagon = null
//            if((Math.random() * 10).toInt % 9 == 0) {
          newHex = new ImageHexagon(center, radius, scaleFactor)
//            } else if((Math.random() * 5).toInt % 4 == 0) {
//              newHex = new EmptyHexagon(center, radius, scaleFactor)
//            } else {
//              newHex = new Hexagon(center, radius, scaleFactor)
//              newHex.asInstanceOf[Hexagon].addScratchShapes
//            }

          visibleHexagons.put(roundedCenter, newHex)
          // redraw new hexagon
          newHex.redraw(scaleFactor)
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
    val startTime = System.nanoTime
    //console.log("update viewPort with Bounds:" + viewPort.getView.bounds.right, viewPort.getView.bounds.top, viewPort.getView.bounds.left, viewPort.getView.bounds.bottom)
    //console.log("SIZE OF VISIBLE HEXAGONS: ", visibleHexagons.size, printCoordinates(visibleHexagons))


    // Remove invisible hexagons
    for(vH <- visibleHexagons) {
      if(isOutOfScope(vH._2.getCenter, vH._2.getRadius)) {
        vH._2.destroy
        visibleHexagons.remove(vH._1)
      }
    }
    reportDuration("Viewer::remove invisible", startTime)

    // Assign new hexagons and redraw them when appear
    val assignNeighboursTime = System.nanoTime
    visibleHexagons.foreach(_._2.assignNeighbours)
    reportDuration("Viewer::remove invisible", assignNeighboursTime)

    if(forceRedrawVisible) {
      console.log("forced redraw of all hexagons")
      visibleHexagons.foreach(_._2.redraw(viewPort.scaleFactor))
    }
    // We don't redraw all Hexagons here (meight not be necessary)
    reportDuration("Viewer::updateView", startTime)
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
      this.clusterList.foreach(_.redraw(viewPort.scaleFactor))
      updateView(forceRedrawVisible = true)

    }
  }

}
