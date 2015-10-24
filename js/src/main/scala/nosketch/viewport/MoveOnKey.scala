package nosketch.viewport

import org.scalajs.dom._

import scala.scalajs.js.Date

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
case class MoveOnKey(var event: KeyboardEvent) {
  var timeUpdated: Double = 0d

  var startTime = Date.now()
}
