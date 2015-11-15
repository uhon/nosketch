package nosketch.io

import org.scalajs.dom._
import org.scalajs.dom.raw.MouseEvent
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

  def onMouseMove(event: ToolEvent): Unit = {}
  def onMouseDrag(event: ToolEvent): Unit = {}
  def onMouseDown(event: ToolEvent): Unit = {}
  def onMouseUp(event: ToolEvent): Unit = {}
  def onMouseScroll(event: WheelEvent): Unit = {}
}
