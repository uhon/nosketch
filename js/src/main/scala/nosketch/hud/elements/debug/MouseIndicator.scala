package nosketch.hud.elements.debug

import nosketch.hud.DebugHUDElement
import nosketch.io.MouseEventDistributor
import nosketch.io.MouseEventListener
import nosketch.viewport.ViewPort
import org.scalajs.dom.WheelEvent
import paperjs.Basic.Point
import paperjs.Items.{Item, Group}
import paperjs.Styling.Color
import paperjs.Tools.ToolEvent
import paperjs.Typography.PointText

import scala.scalajs.js

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class MouseIndicator(viewPort: ViewPort) extends DebugHUDElement with MouseEventListener {

  var text: PointText = null
  var mousePosition = new Point(0)

  override var position: Point = _


  MouseEventDistributor.registerToMouseEvents(this)

  override def onMouseMove(event: ToolEvent): Unit = {
    mousePosition = event.point
    redraw(viewPort)
  }

  override def redraw(viewPort: ViewPort) = {
    layer.activate
    layer.removeChildren()

    text = new PointText(position)
    text.fillColor = Color(255, 255, 255, 0.9)
    text.content = "mouse: " + mousePosition.toString
    text.fontSize = 12 / viewPort.getView.zoom * viewPort.scaleFactor
    layer.bringToFront()

  }

}