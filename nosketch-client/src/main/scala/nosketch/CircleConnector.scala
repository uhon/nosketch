package nosketch

import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Paths.Path
import paperjs.Styling.Color

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class CircleConnector(hexagon: Hexagon, orientation: Int, scaleFactor: Double) extends NosketchObject {
  var connector: Path = null

  redraw(scaleFactor)

  override def redraw(scaleFactor: Double): Unit = {

    console.log("draw circle with center at: " + hexagon.getCenter)
    if (connector != null) {
      connector.remove()
    }

    val vector = new Point(0).add(new Point(hexagon.getCircleCanvas.getRadius / 2 * scaleFactor, 0))
    vector.angle += orientation * 60

    val center = new Point(hexagon.getCenter.x * scaleFactor, hexagon.getCenter.y * scaleFactor).add(vector)

    connector = Path.Circle(center, hexagon.getRadius / 30 * scaleFactor)


    connector.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
  }


//  val center = Point(cSize / 2) + vector
//  console.log("draw connector at center: " + center)
//
//  //connector = connector.center = vector
//  connector.fillColor = Color("#336699")
//  vector.angle += 60
//  connectors = connectors.:+(connector)



  def getPath = connector
}
