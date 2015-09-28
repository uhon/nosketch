package nosketch

import nosketch.components.Hexagon
import nosketch.controls.ControlHandler
import nosketch.io.Freehand
import nosketch.viewport.ViewPort
import org.scalajs.dom._
import paperjs.Typography.PointText
import scala.scalajs.js._
import scala.scalajs.js.annotation.JSExport
import paperjs._
import Basic._,Paths._,Styling._,Tools._

@JSExport
object Drawer extends ViewportSubscriber {

  var hexagon: Hexagon = null

  var freeHand: Freehand = null

  var viewPort: ViewPort= null
  

  @JSExport
  def startDrawer(canvas: html.Canvas): Unit = {
    viewPort = new ViewPort(canvas, this)
    viewPort.init

    hexagon = new Hexagon(viewPort.center, viewPort.defaultPlaygroundSize / 2, viewPort.scaleFactor, true)

    freeHand = new Freehand(hexagon, viewPort.scaleFactor)
    ControlHandler(freeHand)


    val tool = Tool()

    tool.onMouseDrag = (event: ToolEvent) => freeHand.mouseDrag(event)
    tool.onMouseDown = (event: ToolEvent) => freeHand.mouseDown(event)
    tool.onMouseUp = (event: ToolEvent) => freeHand.finishShape()


    onScale
    Paper.view.draw()
  }

  override def onScale = {
    if(hexagon != null) {
      hexagon.redraw(viewPort.scaleFactor)
      freeHand.scaleFactor = viewPort.scaleFactor
    }
  }

  def onZoom = {}
}