package nosketch.util

import org.scalajs.dom._
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.jquery._
import paperjs.Tools.{ToolEvent, Tool}
import paperjs.Basic.Point

import scala.scalajs.js

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object MouseEventDistributor {
  var currentMousePosition = new Point(0, 0)
  val tool = Tool()
  var mouseIsDown = false
  var currentTimeout = null

  tool.onMouseDrag = (event: ToolEvent) => observers.foreach(_.onMouseDrag(event))
  tool.onMouseDown = (event: ToolEvent) => observers.foreach(_.onMouseDown(event))
  tool.onMouseUp = (event: ToolEvent) => observers.foreach(_.onMouseUp(event))
  tool.onMouseMove = (event: ToolEvent) => { currentMousePosition = event.point; observers.foreach(_.onMouseMove(event)) }
  window.onmousedown = (event: MouseEvent) => { mouseIsDown = true; observers.foreach(_.onRealMouseDown(event))}
  window.onmouseup = (event: MouseEvent) => { mouseIsDown = false; observers.foreach(_.onRealMouseUp(event))}
  window.onmousemove = (event: MouseEvent) => { if(mouseIsDown)  observers.foreach(_.onRealMouseDrag(event))  else observers.foreach(_.onRealMouseMove(event))}

  document.getElementById("canvas").addEventListener("DOMMouseScroll", (event: js.Dynamic) => {
    event.preventDefault()
    observers.foreach(o => {
      console.log(event)
      o.onMouseScrollFirefox(
        event.selectDynamic("detail").asInstanceOf[Double]
      )
    })
  })
  //jQuery("#canvas").bind("onmousedown", (event: MouseEvent) => { mouseIsDown = true; observers.foreach(_.onRealMouseDown(event))}

  window.onmousewheel = (event: WheelEvent) => { event.preventDefault(); observers.foreach(_.onMouseScroll(event))}


  private var observers: List[MouseEventListener] = Nil

  def registerToMouseEvents(observer: MouseEventListener) = {
    observers ::= observer
  }

}
