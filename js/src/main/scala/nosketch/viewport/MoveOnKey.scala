package nosketch.viewport

import org.scalajs.dom._

import scala.scalajs.js.Date

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
case class MoveOnKey(event: KeyboardEvent) {
  var startTime = Date.now()
}
