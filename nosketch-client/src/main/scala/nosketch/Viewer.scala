package nosketch

import java.awt.event.MouseWheelEvent

import nosketch.components.Cluster
import nosketch.viewport.ViewPort
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Paper

import scala.scalajs.js.annotation.JSExport

@JSExport
object Viewer extends Playground {

  var viewPort: ViewPort = null

  var clusterList: List[Cluster] = List()

  @JSExport
  def startViewer(canvas: html.Canvas): Unit = {
    viewPort = new ViewPort(canvas, this)

    // Initialize the ViewPort
    viewPort.init

    clusterList ::= new Cluster(viewPort.center, viewPort.scaleFactor)


    update
    Paper.view.draw
  }


  def update = {
    if(viewPort != null)
      // console.log("redraw clusters with scaleFactor", 1 + viewPort.zoomFactor)
      this.clusterList.foreach(_.redraw(viewPort.scaleFactor))
  }
}