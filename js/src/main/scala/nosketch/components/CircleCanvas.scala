package nosketch.components

import nosketch.components.PathObject
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Paths.Path
import paperjs.Styling.Color

class CircleCanvas(hexagon: Hexagon, scaleFactor: Double) extends PathObject {
  var circle: Path = null

  redraw(scaleFactor)

  override def redraw(scaleFactor: Double): Unit = {

    //// console.log("draw circle with center at: " + hexagon.getCenter)
    if (circle != null) {
      circle.remove()
    }

    console.log("getRadius: ", getRadius)
    val center = new Point(hexagon.getCenter.x * scaleFactor, hexagon.getCenter.y * scaleFactor)

    circle = Path.Circle(center, 0.5 * getRadius * scaleFactor)

    //circle.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
    circle.fillColor = Color(233, 233, 170, 0.4)
  }

  override def getPath: Path = circle

  def getRadius = CircleCanvas.getRadiusForInnerCircle(hexagon.getRadius)

}

object CircleCanvas {
  def getRadiusForInnerCircle(radius: Double) = Math.sqrt(3) * radius
}