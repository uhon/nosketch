package nosketch.viewport

import org.scalajs.dom._

import scala.scalajs.js.Date

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
case class SlideAction(var event: KeyboardEvent, var startTime: Double = Date.now()) {}
