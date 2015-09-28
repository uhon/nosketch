package nosketch.hud.elements.debug

import nosketch.components.NosketchObject
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
class ZoomIndicator(viewPort: ViewPort) extends TextIndicator {

  override var content: String = _

  override def redraw(viewPort: ViewPort) = {
    content = "Zoom: " + viewPort.getView.zoom.toString
    super.redraw(viewPort)
  }
}
