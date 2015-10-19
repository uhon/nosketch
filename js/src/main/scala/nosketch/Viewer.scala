package nosketch

import java.awt.event.MouseWheelEvent

import nosketch.components._
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{FPSIndicator, MouseIndicator, TextIndicator}
import nosketch.util.Profiler._
import nosketch.viewport.ViewPort
import paperjs.Paper._
import scala.collection.mutable
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.{PaperScope, Paper}

import scala.scalajs.js.Any
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._

@JSExport
object Viewer extends scala.scalajs.js.JSApp with ViewportSubscriber {

  var visibleHexagons: mutable.Map[Point, VisibleHexagon] = mutable.Map()

  var viewPort: ViewPort = null

  var clusterList: List[Cluster] = List()


  /**
   * Main Entry-Point when not triggered by Script-Tag in Html-Head
   */
  @JSExport
  def main() = {
    startViewer(document.getElementById("canvas").asInstanceOf[html.Canvas])
  }

  /**
   * starts the Viewer by creating and initalizing ViewPort, initHexagons, adding HUD and rezising the View (to the window size)
   * @param canvas
   */
  @JSExport
  def startViewer(canvas: html.Canvas): Unit = {
    if(canvas == null) { return }

    viewPort = new ViewPort(canvas, this)


    // Initialize the ViewPort
    viewPort.init


    // enable Debug HUD and add Content to it
    DebugHUD.enabled = true
    DebugHUD.addElement(new TextIndicator(() => "scale: " + viewPort.scaleFactor.toString))
    DebugHUD.addElement(new TextIndicator(() => "zoom: " + viewPort.getView.zoom.toString))
    DebugHUD.addElement(new TextIndicator(() => "delta-c: " + viewPort.getOffsetVector))
    DebugHUD.addElement(new TextIndicator(() => "visibleHexagons: " + this.visibleHexagons.size))
    DebugHUD.addElement(new TextIndicator(() => "layers: " + project.layers.size))
    DebugHUD.addElement(new MouseIndicator(viewPort))
    DebugHUD.addElement(FPSIndicator)

    //clusterList ::= new Cluster(viewPort.center, viewPort.scaleFactor)


    viewPort.resizeWindow // triggers on scale
    initHexagons
    Paper.view.draw()

    removeEverythingPeriodically
  }


  def initHexagons = {
    val startTime = System.nanoTime
    val initialHex = new Hexagon(viewPort.center, 250, 1)
    initialHex.assignNeighbours
    //reportDuration("Viewer::initHexagons", startTime)
  }

  def isOutOfScope(center: Point, radius: Double) = {
    val startTime = System.nanoTime
    val viewportBounds = viewPort.getView.bounds

    val distE = (viewportBounds.right * viewPort.scaleFactor - center.x * viewPort.scaleFactor)
    val distN = (viewportBounds.top * viewPort.scaleFactor - center.y * viewPort.scaleFactor )
    val distW = (viewportBounds.left * viewPort.scaleFactor - center.x * viewPort.scaleFactor )
    val distS = (viewportBounds.bottom * viewPort.scaleFactor - center.y * viewPort.scaleFactor )


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
   * @param center the center position of the new Hexagon
   * @param radius radius of the new Hexagon
   * @param scaleFactor scale Factor to Scale the whole thing
   * @return
   */
  def findOrCreateHexagon(center: Point, radius: Double, scaleFactor: Double): AbstractHexagon = {
    val startTime = System.nanoTime
    try {
      val roundedCenter = center.round()
      visibleHexagons.find(p => p._1.equals(roundedCenter)) match {
        case Some(x) => x._2
        case None => {
          if (Viewer.isOutOfScope(center, radius)) {
            //console.log("Not creating Hexagon at Pos", center.toString())
            PhantomHexagon
          } else {
            // TODO: call cluster logic from here to create hexagons with sketches
            // for now just test-shapes and Hexagons Types shown
            var newHex:VisibleHexagon = null
//            if((Math.random() * 10).toInt % 9 == 0) {
              newHex = new ImageHexagon(center, radius, scaleFactor);
//            } else if((Math.random() * 5).toInt % 4 == 0) {
//              newHex = new EmptyHexagon(center, radius, scaleFactor)
//            } else {
//              newHex = new Hexagon(center, radius, scaleFactor)
//              newHex.asInstanceOf[Hexagon].addScratchShapes
//            }

            visibleHexagons.put(roundedCenter, newHex)
            // redraw new hexagon
            newHex.redraw(scaleFactor)
            newHex
          }
        }
      }
    } finally reportDuration("Viewer::findOrCreateHexagon", startTime)
  }


  def printCoordinates(visibleHexagons: mutable.Map[Point, VisibleHexagon]): String = {
    visibleHexagons.map(vH => s"""[${vH._1.x}:${vH._1.y}]""").mkString(",")
  }

  /**
   * Updates the view and redraws all Hexagon which will newly appear on screen
   * @param forceRedrawVisible
   */
  def updateView(forceRedrawVisible: Boolean = false) = {
    val startTime = System.nanoTime
    console.log("update viewPort with Bounds:" + viewPort.getView.bounds.right, viewPort.getView.bounds.top, viewPort.getView.bounds.left, viewPort.getView.bounds.bottom)
    //console.log("SIZE OF VISIBLE HEXAGONS: ", visibleHexagons.size, printCoordinates(visibleHexagons))

    // Remove invisible hexagons
    for(vH <- visibleHexagons) {
      if(isOutOfScope(vH._2.getCenter, vH._2.getRadius)) {
        vH._2.destroy
        visibleHexagons.remove(vH._1)
      }
    }

    // Assign new hexagons and redraw them when appear
    visibleHexagons.foreach(_._2.assignNeighbours)

    if(forceRedrawVisible) {
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

  def removeEverythingPeriodically = {
    //testing method
//    setInterval(() => {
//      project.activeLayer.removeChildren()
//      initHexagons
//      updateView()
//    }, 1000)
  }

}
