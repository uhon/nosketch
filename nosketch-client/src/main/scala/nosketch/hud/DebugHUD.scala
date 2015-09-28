package nosketch.hud

import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import nosketch.components.ZoomAwareObject

import scala.scalajs.js.Object

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object DebugHUD extends ZoomAwareObject {
  var enabled = true
  val verticalOffset = 20
  var elements = List[DebugHUDElement]()
  var position = new Point(10, 10)

  def addElement(element: DebugHUDElement) = {

    elements +:= element
  }

  override def redraw(viewPort: ViewPort) = {
    if(enabled) {
      elements.zipWithIndex.foreach({ case(e,i) => e.position = viewPort.cornerTopLeft().add(new Point(10 / viewPort.getView.zoom, (i + 1) * verticalOffset / viewPort.getView.zoom)) })

      //element.position = position.add(new Point(0, elements.size * verticalOffset))

      elements.foreach(_.redraw(viewPort))
    }
  }
}
