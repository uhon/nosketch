package nosketch.hud

import nosketch.components.ZoomAwareObject
import paperjs.Basic.Point
import paperjs.Items.{Item, Layer}

import scala.scalajs.js

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait DebugHUDElement extends ZoomAwareObject {
  val layer = new Layer(new js.Array[Item]())
  var position: Point
}
