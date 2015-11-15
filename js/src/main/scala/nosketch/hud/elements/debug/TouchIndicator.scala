package nosketch.hud.elements.debug

import javafx.scene.input.TouchEvent

import nosketch.hud.DebugHUDElement
import nosketch.io.{TouchEventDistributor, TouchEventListener, MouseEventDistributor, MouseEventListener}
import nosketch.viewport.ViewPort
import org.scalajs.dom.html.Canvas
import paperjs.Basic.Point
import paperjs.Styling.Color
import paperjs.Tools.ToolEvent
import paperjs.Typography.PointText
import org.scalajs.dom._

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class TouchIndicator(viewPort: ViewPort) extends DebugHUDElement with TouchEventListener {

  var text: PointText = null
  var touchPosition = new Point(0)

  override var position: Point = _


  TouchEventDistributor.registerTouchListener(document.getElementsByTagName("canvas").item(0).asInstanceOf[Canvas], this, viewPort)

  override def onDrag(eventPoint: Point): Unit = {
    touchPosition = eventPoint
    redraw(viewPort)
  }

  override def redraw(viewPort: ViewPort) = {
    // TODO: Reregister
    layer.activate
    layer.removeChildren()

    text = new PointText(position)
    text.fillColor = Color(255, 255, 255, 0.9)
    text.content = "touch-center: " + touchPosition.toString
    text.fontSize = 12 / viewPort.getView.zoom * viewPort.scaleFactor
    layer.bringToFront()

  }

}