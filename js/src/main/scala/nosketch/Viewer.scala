package nosketch

import java.awt.event.MouseWheelEvent

import nosketch.components.Cluster
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{MouseIndicator, TextIndicator}
import nosketch.viewport.ViewPort
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Paper

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._

@JSExport
object Viewer extends scala.scalajs.js.JSApp with ViewportSubscriber {

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

    // enable Debug HUD and add Content to it
    DebugHUD.enabled = true
    DebugHUD.addElement(new TextIndicator(() => "scale: " + viewPort.scaleFactor.toString))
    DebugHUD.addElement(new TextIndicator(() => "zoom: " + viewPort.getView.zoom.toString))
    DebugHUD.addElement(new TextIndicator(() => "delta-c: " + viewPort.getOffsetVector))
    DebugHUD.addElement(new MouseIndicator(viewPort))

    clusterList ::= new Cluster(viewPort.center, viewPort.scaleFactor)

    onScale

    Paper.view.draw()
  }



  override def onZoom = {
    if (viewPort != null) {
      DebugHUD.redraw(viewPort)

    }
  }

  override def onScale = {
    if (viewPort != null) {
      // console.log("redraw clusters with scaleFactor", 1 + viewPort.zoomFactor)
      this.clusterList.foreach(_.redraw(viewPort.scaleFactor))
      DebugHUD.redraw(viewPort)
    }
  }
}