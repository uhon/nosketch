package nosketch.util

import org.scalajs.dom._

import org.scalajs.dom.raw.MouseEvent
import nosketch.viewport.ViewPort
import org.w3c.dom.events
import paperjs.Basic.Point
import paperjs.Tools.ToolEvent

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait MouseEventListener {
  def onMouseScrollFirefox(deltaY: Double): Unit = {}

  def onRealMouseDown(event: MouseEvent): Unit = {}
  def onRealMouseUp(event: MouseEvent): Unit = {}
  def onRealMouseMove(event: MouseEvent): Unit = {}
  def onRealMouseDrag(event: MouseEvent): Unit = {}

  def onMouseMove(event: ToolEvent)
  def onMouseDrag(event: ToolEvent)
  def onMouseDown(event: ToolEvent)
  def onMouseUp(event: ToolEvent)
  def onMouseScroll(event: WheelEvent)
}
