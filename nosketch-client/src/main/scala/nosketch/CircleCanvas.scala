package nosketch

import nosketch.{Hexagon, NosketchObject}
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Paths.Path
import paperjs.Styling.Color

class CircleCanvas(hexagon: Hexagon, scaleFactor: Double) extends NosketchObject {
  var circle: Path = null

  redraw(scaleFactor)

  override def redraw(scaleFactor: Double): Unit = {

    console.log("draw circle with center at: " + hexagon.getCenter)
    if (circle != null) {
      circle.remove()
    }

    var center = new Point(hexagon.getCenter.x * scaleFactor, hexagon.getCenter.y * scaleFactor)

    circle = Path.Circle(center, 0.5 * getRadius * scaleFactor)

    //circle.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
    circle.fillColor = Color("#e9e9aa")
  }

  override def getPath: Path = circle

  def getRadius = Math.sqrt(3) * hexagon.getRadius
}