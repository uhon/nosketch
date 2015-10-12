package nosketch.util

import org.scalajs.dom.WheelEvent
import org.scalajs.dom.window
import paperjs.Tools.{ToolEvent, Tool}
import paperjs.Basic.Point

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object MouseEventDistributor {
  var currentMousePosition = new Point(0, 0)
  val tool = Tool()

  tool.onMouseDrag = (event: ToolEvent) => observers.foreach(_.onMouseDrag(event))
  tool.onMouseDown = (event: ToolEvent) => observers.foreach(_.onMouseDown(event))
  tool.onMouseUp = (event: ToolEvent) => observers.foreach(_.onMouseUp(event))
  tool.onMouseMove = (event: ToolEvent) => { currentMousePosition = event.point; observers.foreach(_.onMouseMove(event)) }
  window.onmousewheel = (event: WheelEvent) => { event.preventDefault(); observers.foreach(_.onMouseScroll(event))}


  private var observers: List[MouseEventListener] = Nil

  def registerToMouseEvents(observer: MouseEventListener) = {
    observers ::= observer
  }

}
