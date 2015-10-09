package nosketch.hud

import nosketch.components.ZoomAwareObject
import paperjs.Basic.Point
/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait DebugHUDElement extends ZoomAwareObject {
  var position: Point
}
