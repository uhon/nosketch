package nosketch.hud

import nosketch.components.ZoomAwareObject
import nosketch.util.NSTools
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

  private val uid = NSTools.generateID()


  def render = {
    div (id := uid, `class` := "row indicator") (
      div (`class` := "key col-xs-9") (key),
      div (`class` := "value col-xs-3") (value)
    )
}

  def setValue(newValue: String) {
    value = newValue
    $(s"#$uid .value").text(value)
  }
}
