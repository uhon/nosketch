package nosketch.hud.elements.debug

import nosketch.hud.DebugHUDElement
import nosketch.util.{MouseEventDistributor, MouseEventListener}
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
  var group = Group(js.Array[Item]())

  override var position: Point = _


  MouseEventDistributor.registerToMouseEvents(this)

  override def onMouseDrag(event: ToolEvent) = {}

  override def onMouseDown(event: ToolEvent) = {}

  override def onMouseUp(event: ToolEvent) = {}


  override def onMouseMove(event: ToolEvent): Unit = {
    mousePosition = event.point
    redraw(viewPort)
  }

  override def onMouseScroll(event: WheelEvent): Unit = {}

  override def redraw(viewPort: ViewPort) = {
    group.removeChildren()
    group = Group(js.Array[Item]())
    text = new PointText(position)
    text.fillColor = Color(255, 255, 255, 0.9)
    text.content = mousePosition.toString
    text.fontSize = 12 / viewPort.getView.zoom * viewPort.scaleFactor
    group.addChild(text)
  }

}