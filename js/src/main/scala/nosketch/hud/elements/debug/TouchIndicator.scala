package nosketch.hud.elements.debug

import javafx.scene.input.TouchEvent

import nosketch.hud.DebugHUDElement
import nosketch.io.{TouchEventDistributor, TouchEventListener, MouseEventDistributor, MouseEventListener}
import nosketch.viewport.ViewPort
import org.scalajs.dom.html.Canvas
import paperjs.Basic.Point
import paperjs.Styling.Color
import paperjs.Tools.ToolEvent
import paperjs.Typography.PointText
import org.scalajs.dom._

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class TouchIndicator(override val key: String) extends DebugHUDElement with TouchEventListener {

  override def onDrag(eventPoint: Point): Unit = {
    setValue(eventPoint)
  }

  override def update: Unit = {}
}