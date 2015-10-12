package nosketch.hud.elements.debug

import nosketch.hud.DebugHUDElement
import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Styling.Color
import paperjs.Typography.PointText

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object FPSIndicator extends DebugHUDElement {
  var text: PointText = null
  var fps = 0
  override var position: Point = _

  override def redraw(viewPort: ViewPort): Unit = {
    layer.activate
    layer.removeChildren()

    text = new PointText(position)
    text.fillColor = Color(255, 255, 255, 0.9)
    text.content = "FPS: " + fps.toString
    text.fontSize = 12 / viewPort.getView.zoom * viewPort.scaleFactor
    layer.bringToFront()
  }
}
