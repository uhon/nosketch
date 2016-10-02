package nosketch.hud.elements.debug

import nosketch.hud.DebugHUDElement
import nosketch.io.{MouseEventDistributor, MouseEventListener}
import paperjs.Tools.ToolEvent

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class SimpleIndicator(override val key: String) extends DebugHUDElement with MouseEventListener {
  override def update: Unit = {}
}