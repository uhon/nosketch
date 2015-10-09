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
class TextIndicator(contentProducer: () => String) extends DebugHUDElement {

  var text: PointText = null
  var group = Group(js.Array[Item]())

  override var position: Point = _

  def redraw(viewPort: ViewPort) = {
    group.removeChildren()
    group = Group(js.Array[Item]())
    text = new PointText(position)
    text.fillColor = Color(255, 255, 255, 0.9)
    text.content = contentProducer()
    text.fontSize = 12 / viewPort.getView.zoom * viewPort.scaleFactor
    group.addChild(text)
  }
}
