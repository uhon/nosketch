package nosketch.hud.elements.debug

import nosketch.hud.DebugHUDElement
import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Items.{Item, Group}
import paperjs.Styling.Color
import paperjs.Typography.PointText

import scala.scalajs.js
import scala.scalajs.js.Math

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
abstract class TextIndicator extends DebugHUDElement {

  var text: PointText = null
  var group = Group(js.Array[Item]())

  var content: String

  override var position: Point = _

  def redraw(viewPort: ViewPort) = {
    group.removeChildren()
    text = new PointText(position)
    text.fillColor = Color(255, 255, 255, 0.9)
    text.content = content
    text.fontSize = 15 / viewPort.getView.zoom
    group.addChild(text)
  }
}
