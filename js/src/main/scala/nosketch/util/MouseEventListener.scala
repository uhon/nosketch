package nosketch.util

import org.scalajs.dom._

import nosketch.viewport.ViewPort
import paperjs.Tools.ToolEvent

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait MouseEventListener {
  def onMouseMove(event: ToolEvent)
  def onMouseDrag(event: ToolEvent)
  def onMouseDown(event: ToolEvent)
  def onMouseUp(event: ToolEvent)
  def onMouseScroll(event: WheelEvent)
}
