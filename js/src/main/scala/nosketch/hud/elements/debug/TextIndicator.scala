package nosketch.hud.elements.debug

import nosketch.hud.DebugHUDElement
import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Items.{Group, Item}
import paperjs.Styling.Color
import paperjs.Typography.PointText

import scala.scalajs.js
import scala.scalajs.js.Math
import scalatags.Text.TypedTag
import nosketch.components.ZoomAwareObject
import paperjs.Basic.Point
import paperjs.Items.{Item, Layer}
import vongrid.utils.Tools
import org.querki.jquery._

import scalatags.Text.all._
import org.scalajs.dom._

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class TextIndicator(override val key: String, contentProducer: () => String) extends DebugHUDElement {

  override def render: TypedTag[String] = {
    setValue(contentProducer())
    super.render
  }

  override def update: Unit = {
    setValue(contentProducer())
  }
}
