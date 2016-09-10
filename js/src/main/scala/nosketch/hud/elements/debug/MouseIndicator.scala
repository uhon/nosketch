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
class MouseIndicator(key: String) extends DebugHUDElement with MouseEventListener {

  MouseEventDistributor.registerToMouseEvents(this)

  override def onMouseMove(event: ToolEvent): Unit = {
    setValue(s"${event.point.x},${event.point.y}")
  }

  override def update: Unit = {}

}