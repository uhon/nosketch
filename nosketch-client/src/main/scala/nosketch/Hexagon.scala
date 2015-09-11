package nosketch

import paperjs.Basic.Point
import paperjs.Paths.Path
import org.scalajs.dom._
import paperjs.Styling.Color

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class Hexagon(center: Point, radius: Double, scaleFactor: Double) extends NosketchObject {

  var hex: Path = null
  var circleCanvas: CircleCanvas = null
  var connectors: List[CircleConnector] = List()


  // Setup circleCanvas

  circleCanvas = new CircleCanvas(this, scaleFactor)

  // Create Connectors
  for(i <- 0 to 5) {
    connectors = connectors :+ new CircleConnector(this, i, scaleFactor)
  }



  redraw(scaleFactor)

  def redraw(scaleFactor: Double) = {

    console log "draw hexagon with center at: " + center
    if (hex != null) hex remove()

    val c = new Point(center.x * scaleFactor, center.y * scaleFactor)
    hex = Path RegularPolygon(c, 6, radius * scaleFactor)
    hex rotate 30

    //hex.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
    hex fillColor = Color("#e9e9ff")

    circleCanvas redraw scaleFactor

    for(c <- this.connectors) {
      c redraw scaleFactor
    }
  }

  def getCircleCanvas = circleCanvas

  def getRadius: Double = radius

  def getCenter: Point = center

  override def getPath: Path = hex
}
