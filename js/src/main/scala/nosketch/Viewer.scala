package nosketch

import java.awt.event.MouseWheelEvent

import nosketch.components._
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{MouseIndicator, TextIndicator}
import nosketch.viewport.ViewPort
import paperjs.Paper._
import scala.collection.mutable
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Paper

import scala.scalajs.js.Any
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._

@JSExport
object Viewer extends scala.scalajs.js.JSApp with ViewportSubscriber {

  var visibleHexagons: mutable.Map[Point, VisibleHexagon] = mutable.Map()

  var viewPort: ViewPort = null

  var clusterList: List[Cluster] = List()

  @JSExport
  def main() = {
    startViewer(document.getElementById("canvas").asInstanceOf[html.Canvas])
  }

  @JSExport
  def startViewer(canvas: html.Canvas): Unit = {
    if(canvas == null) { return }

    viewPort = new ViewPort(canvas, this)


    // Initialize the ViewPort
    viewPort.init

    initHexagons

    // enable Debug HUD and add Content to it
    DebugHUD.enabled = true
    DebugHUD.addElement(new TextIndicator(() => "scale: " + viewPort.scaleFactor.toString))
    DebugHUD.addElement(new TextIndicator(() => "zoom: " + viewPort.getView.zoom.toString))
    DebugHUD.addElement(new TextIndicator(() => "delta-c: " + viewPort.getOffsetVector))
    DebugHUD.addElement(new TextIndicator(() => "visibleHexagons: " + this.visibleHexagons.size))
    DebugHUD.addElement(new MouseIndicator(viewPort))

    //clusterList ::= new Cluster(viewPort.center, viewPort.scaleFactor)


    viewPort.resizeWindow() // triggers on scale
  }


  def initHexagons = {
    val initialHex = new EmptyHexagon(viewPort.center, 200, 1)
    initialHex.assignNeighbours
  }

  def isOutOfScope(center: Point, radius: Double) = {
    val viewportBounds = viewPort.getView.bounds

    val distE = (viewportBounds.right - center.x * viewPort.scaleFactor)
    val distN = (viewportBounds.top - center.y * viewPort.scaleFactor )
    val distW = (viewportBounds.left - center.x * viewPort.scaleFactor )
    val distS = (viewportBounds.bottom - center.y * viewPort.scaleFactor )


    (distE < 0 && Math.abs(distE) > radius / viewPort.getView.zoom) ||
    (distN > 0 && Math.abs(distN) > radius / viewPort.getView.zoom) ||
    (distW > 0 && Math.abs(distW) > radius / viewPort.getView.zoom) ||
    (distS < 0 && Math.abs(distS) > radius / viewPort.getView.zoom)

  }


  def findOrCreateHexagon(center: Point, radius: Double, scaleFactor: Double): AbstractHexagon = {
    val roundedCenter = center.round()
    visibleHexagons.find(p => p._1.equals(roundedCenter)) match {
      case Some(x) => x._2
      case None => {
        if(Viewer.isOutOfScope(center, radius)) {
          //console.log("Not creating Hexagon at Pos", center.toString())
          PhantomHexagon
        } else {
          //val newHex = new EmptyHexagon(center, radius, scaleFactor)// TODO: Draw something else than EmptyHexagon by checking if it belongs to a cluster
          val newHex = new Hexagon(center, radius, scaleFactor);
          newHex.addScratchShapes

          visibleHexagons.put(roundedCenter, newHex)
          newHex
        }
      }
    }
  }


  def printCoordinates(visibleHexagons: mutable.Map[Point, VisibleHexagon]): String = {
    visibleHexagons.map(vH => s"""[${vH._1.x}:${vH._1.y}]""").mkString(",")
  }

  def updateView = {
    console.log("viewPort Bounds:" + viewPort.getView.bounds.right, viewPort.getView.bounds.top, viewPort.getView.bounds.left, viewPort.getView.bounds.bottom)
    console.log("SIZE OF VISIBLE HEXAGONS: ", visibleHexagons.size, printCoordinates(visibleHexagons))

    // Remove invisible hexagons
    for(vH <- visibleHexagons) {
      if(isOutOfScope(vH._2.getCenter, vH._2.getRadius)) {
        vH._2.destroy
        visibleHexagons.remove(vH._1)
      }
    }

    // Assign new hexagons
    visibleHexagons.foreach(_._2.assignNeighbours)

    visibleHexagons.foreach(_._2.redraw(viewPort.scaleFactor))
    // TODO: Experimental, does it help with performance?
    //viewPort.getView.update()
  }

  override def onZoom = {
    if (viewPort != null) {
      updateView
      DebugHUD.redraw(viewPort)
    }
  }

  override def onScale = {
    if (viewPort != null) {
      // console.log("redraw clusters with scaleFactor", 1 + viewPort.zoomFactor)
      this.clusterList.foreach(_.redraw(viewPort.scaleFactor))
      updateView
      DebugHUD.redraw(viewPort)
    }
  }
}