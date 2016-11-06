package nosketch.controls

import nosketch.io.Freehand
import org.scalajs.dom.{Element, console}
import org.querki.jquery._


object ControlHandler {
  var freehand:Freehand = null
  object StrokeWidth {
    val Small= 2
    val Medium=5
    val Large=10
  }

  def setStrokeWith(element: Element, size: Int): Unit = {
    // console.log("New StrokeWidth is:", size.toString.toInt)
    $("#controlBar .control") removeClass "active"
    $(element) addClass "active"
    freehand.currentStrokeWidth = size.toString.toInt
  }

  def apply(freehand: Freehand): Unit = {
    this.freehand = freehand
    $("#strokeWidthSmall").click((e: Element) => setStrokeWith(e, StrokeWidth.Small))
    $("#strokeWidthMedium").click((e: Element) => setStrokeWith(e, StrokeWidth.Medium))
    $("#strokeWidthLarge").click((e: Element) => setStrokeWith(e, StrokeWidth.Large))
  }
}