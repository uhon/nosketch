package nosketch.hud

import nosketch.components.ZoomAwareObject
import paperjs.Basic.Point
import paperjs.Items.{Item, Layer}
import vongrid.utils.Tools
import org.querki.jquery._

import scalatags.Text.all._
/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait DebugHUDElement {
  def update: Unit

  protected val key = ""
  protected var value = ""

  private val uid = Tools.generateID()


  def render = {
    div (id := uid, `class` := "indicator") (
      div (`class` := "key") (key),
      div (`class` := "value") (value)
    )
}

  def setValue(newValue: String) {
    value = newValue
    $(s"#$uid .value").text(value)
  }
}
