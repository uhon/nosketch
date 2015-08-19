package nosketch

import org.scalajs.jquery._
import org.scalajs.dom.console


object ControlHandler {

  object StrokeWidth {
    val Small= 2
    val Medium=5
    val Large=10
  }

  def setStrokeWith(event: JQueryEventObject, size: Int): Unit = {
    console.log("New StrokeWidth is:", size.toString.toInt)
    jQuery("#controlBar .control") removeClass "active"
    jQuery(event.target) addClass "active"
    Drawer.currentStrokeWidth = size.toString.toInt
  }

  def init(): Unit = {
    jQuery("#strokeWidthSmall").click((e: JQueryEventObject) => setStrokeWith(e, StrokeWidth.Small))
    jQuery("#strokeWidthMedium").click((e: JQueryEventObject) => setStrokeWith(e, StrokeWidth.Medium))
    jQuery("#strokeWidthLarge").click((e: JQueryEventObject) => setStrokeWith(e, StrokeWidth.Large))
  }
}