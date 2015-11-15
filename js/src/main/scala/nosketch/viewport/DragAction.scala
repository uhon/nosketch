package nosketch.viewport

import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Tools.ToolEvent

import scala.scalajs.js.Date

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
case class DragAction(var eventPoint: Point, var initialMouseCoordinates: Point, var initialViewCenter: Point) {
}
