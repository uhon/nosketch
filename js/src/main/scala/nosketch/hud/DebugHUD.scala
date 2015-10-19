package nosketch.hud

import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Basic.Rect
import paperjs.Basic.Size
import nosketch.components.ZoomAwareObject
import paperjs.Items.{Layer, Item, Group, Shape}
import paperjs.Items.Shape.Rectangle
import paperjs.Styling.Color

import scala.scalajs.js
import scala.scalajs.js.Object

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object DebugHUD extends ZoomAwareObject {
  val layer = new Layer(new js.Array[Item](0))
  val verticalOffset = 20
  var enabled = true
  var elements = List[DebugHUDElement]()
  var startPosition = new Point(10, 50)
  var rectangle: Shape = null

  def addElement(element: DebugHUDElement) = {

    elements :+= element
  }



  override def redraw(viewPort: ViewPort) = {
    layer.removeChildren()
    layer.activate()
    if(enabled) {
      val currentStartPosition = startPosition.divide(viewPort.getView.zoom).multiply(viewPort.scaleFactor)
      val curPosition = viewPort.cornerTopLeft().add(currentStartPosition)
      val curVerticalOffset = verticalOffset / viewPort.getView.zoom * viewPort.scaleFactor
      val padding = new Point(10, 15).divide(viewPort.getView.zoom).multiply(viewPort.scaleFactor)
      rectangle = Rectangle(
        curPosition.subtract(padding),
        new Size(250 / viewPort.getView.zoom * viewPort.scaleFactor, curVerticalOffset * elements.length + padding.y)
      )
      rectangle.fillColor = new Color(0, 0, 0, 0.7)

      elements.zipWithIndex.foreach({ case(e,i) =>
        e.position =
          new Point(
            curPosition.x,
            curPosition.y + (i + 0.25) * curVerticalOffset
          )
      })


      layer.bringToFront()


      //element.position = position.add(new Point(0, elements.size * verticalOffset))

      elements.foreach(_.redraw(viewPort))
    }
  }
}
