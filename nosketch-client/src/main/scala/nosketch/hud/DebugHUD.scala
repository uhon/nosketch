package nosketch.hud

import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Basic.Rect
import paperjs.Basic.Size
import nosketch.components.ZoomAwareObject
import paperjs.Items.{Item, Group, Shape}
import paperjs.Items.Shape.Rectangle
import paperjs.Styling.Color

import scala.scalajs.js
import scala.scalajs.js.Object

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object DebugHUD extends ZoomAwareObject {
  val verticalOffset = 20
  var group = Group(js.Array[Item]())
  var enabled = true
  var elements = List[DebugHUDElement]()
  var startPosition = new Point(10, 10)
  var rectangle: Shape = null

  def addElement(element: DebugHUDElement) = {

    elements :+= element
  }

  override def redraw(viewPort: ViewPort) = {
    if(enabled) {
      rectangle = null
      group.removeChildren()
      group = Group(js.Array[Item]())

      val curPosition = startPosition.divide(viewPort.getView.zoom).multiply(viewPort.scaleFactor)
      val curVerticalOffset = verticalOffset / viewPort.getView.zoom * viewPort.scaleFactor

      rectangle = Rectangle(viewPort.cornerTopLeft(), new Size(viewPort.getView.size.width / 2, curVerticalOffset * elements.length))
      rectangle.fillColor = new Color(0, 0, 0, 0.3)

      group.addChild(rectangle)

      elements.zipWithIndex.foreach({ case(e,i) =>
          e.position = viewPort.cornerTopLeft().add(
            new Point(
              curPosition.x,
              curPosition.y + (i + 0.25) * curVerticalOffset
            )
          )
      })

      //element.position = position.add(new Point(0, elements.size * verticalOffset))

      elements.foreach(_.redraw(viewPort))
    }
  }
}
