package nosketch.hud.elements.debug

import nosketch.hud.DebugHUDElement

import scalatags.Text.TypedTag

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class IncrementalIndicator(override val key: String) extends DebugHUDElement {
  var counter = 0
  def increment = counter = counter + 1;

  override def update: Unit = {
    setValue(counter)
  }
}
