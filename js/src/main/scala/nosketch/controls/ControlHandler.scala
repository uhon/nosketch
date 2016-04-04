package nosketch.controls

import nosketch.io.Freehand
import org.scalajs.dom.console
import org.querki.jquery._


object ControlHandler {
  var freehand:Freehand = null
  object StrokeWidth {
    val Small= 2
    val Medium=5
    val Large=10
  }

  def setStrokeWith(event: JQueryEventObject, size: Int): Unit = {
    // console.log("New StrokeWidth is:", size.toString.toInt)
    $("#controlBar .control") removeClass "active"
    $(event.target) addClass "active"
    freehand.currentStrokeWidth = size.toString.toInt
  }

  def apply(freehand: Freehand): Unit = {
    this.freehand = freehand
    $("#strokeWidthSmall").click((e: JQueryEventObject) => setStrokeWith(e, StrokeWidth.Small))
    $("#strokeWidthMedium").click((e: JQueryEventObject) => setStrokeWith(e, StrokeWidth.Medium))
    $("#strokeWidthLarge").click((e: JQueryEventObject) => setStrokeWith(e, StrokeWidth.Large))
  }
}