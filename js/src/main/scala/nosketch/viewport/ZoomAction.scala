package nosketch.viewport;


import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Tools.ToolEvent

import scala.scalajs.js.Date


case class ZoomAction(var delta: Double, var initialMouseCoordinates: Point)
