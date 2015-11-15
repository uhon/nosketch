package nosketch.io

import nosketch.viewport.ViewPort
import org.scalajs.dom._
import scala.collection._
import org.scalajs.dom.html._
import org.scalajs.dom.raw.{TouchEvent, MouseEvent}
import paperjs.Basic.Point
import paperjs.Tools.{Tool, ToolEvent}

import scala.scalajs.js

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object TouchEventDistributor {

  private var observers: mutable.Map[TouchEventListener, Canvas] = mutable.Map()

  def reRegisterObservers(viewPort: ViewPort) = {
    observers.foreach(e => {
      e._2.removeEventListener("touchstart", touchStartEvent, useCapture = false)
      e._2.removeEventListener("touchend", touchEndEvent, useCapture = false)
      e._2.removeEventListener("touchmove", touchMoveEvent, useCapture = false)


      observers.remove(e._1)
      registerTouchListener(e._2, e._1, viewPort)
    })

  }


  def registerTouchListener(canvas: Canvas, listener: TouchEventListener, viewPort: ViewPort): Unit = {


    canvas.addEventListener("touchstart", touchStartEvent, useCapture = false)
    canvas.addEventListener("touchend", touchEndEvent, useCapture = false)
    canvas.addEventListener("touchmove", touchMoveEvent, useCapture = false)
    observers.put(listener, canvas)
  }

  val scroll_threshold = 5
  var first_event = true
  var is_clicking = false
  var two_finger = false
  var last_x0 = 0.0
  var last_y0 = 0.0
  var last_x1 = 0.0
  var last_y1 = 0.0

  val getDist = (x0: Double, x1: Double, y0: Double, y1: Double) => {
    Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1))
  }

  val touchStartEvent = (e: TouchEvent) => {
    is_clicking = true
    last_x0 = e.touches(0).clientX
    last_y0 = e.touches(0).clientY
    two_finger = true
    if (e.touches.length == 2) {
      last_x1 = e.touches(1).clientX
      last_y1 = e.touches(1).clientY
    }
    e.preventDefault()
    false
  }
  val touchEndEvent = (e: TouchEvent) => {
    is_clicking = false
    two_finger = false
    last_x0 = 0
    last_x1 = 0
    last_y0 = 0
    last_y1 = 0
    e.preventDefault()
    false
  }
  val touchMoveEvent = (e: TouchEvent) => {
    var is_scroll = false
    var is_pinch = false
    var delta_x = 0.0
    var delta_y = 0.0
    var centerPosition = new Point(0)

    if (two_finger && e.touches.length == 2) {
      val last_dist = getDist(last_x0, last_x1, last_y0, last_y1)
      val now_dist = getDist(e.touches(0).clientX,
        e.touches(1).clientX,
        e.touches(0).clientY,
        e.touches(1).clientY)
      val delta_dist = now_dist - last_dist
      is_scroll = delta_dist > -scroll_threshold && delta_dist < scroll_threshold
      is_pinch = !is_scroll
      delta_x = last_x0 - e.touches(0).clientX
      if (is_scroll) {
        delta_y = last_y0 - e.touches(0).clientY
      } else {
        delta_y = delta_dist
      }
      last_x1 = e.touches(1).clientX
      last_y1 = e.touches(1).clientY
      centerPosition = new Point((e.touches(0).clientX + e.touches(1).clientX) / 2, (e.touches(0).clientY + e.touches(1).clientY) / 2)
    } else {
      delta_x = last_x0 - e.touches(0).clientX
      delta_y = last_y0 - e.touches(0).clientY
      centerPosition = new Point(e.touches(0).clientX, e.touches(0).clientY)
    }
    last_x0 = e.touches(0).clientX
    last_y0 = e.touches(0).clientY
    if (first_event) {
      first_event = false
    } else {
      console.log("touches", e.touches)
      if(is_scroll) {
        observers.foreach(_._1.onZoom(
          (delta_x + delta_y) * 50,
          centerPosition
        ))
      } else {
        observers.foreach(_._1.onDrag(
          centerPosition
        ))
      }




      //////////////
      //        listener.onTouchEvent(
      //          is_clicking,
      //          delta_x, delta_y,
      //          is_pinch, is_scroll
      //        )
    }
    e.preventDefault()
    false
  }
}
