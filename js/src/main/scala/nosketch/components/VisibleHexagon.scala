package nosketch.components

import nosketch.Viewer
import org.scalajs.dom.console
import nosketch.components.PhantomHexagon
import nosketch.components.EmptyHexagon
import nosketch.util.HexConstants
import paperjs.Basic.Point
import paperjs.Paths.Path
import paperjs.Styling.Color

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
abstract class VisibleHexagon(center: Point, radius: Double, scaleFactor: Double, showInnerCircle: Boolean = false) extends AbstractHexagon {
  var neighbours: Array[_ >: AbstractHexagon] =  Array[AbstractHexagon](PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon)

  var hex: Path = null

  val color = Color(Math.random(), Math.random(), Math.random(), 0.3)

  def destroy: Any = {
    hex.remove
    hex = null

    for (i <- 0 to 5) {
      neighbours(i) match {
        case x: VisibleHexagon => x.neighbours(HexConstants.sideMappings(i)) = PhantomHexagon
        case x: AbstractHexagon => {}
      }
    }

    neighbours = null
  }






  def getRadius: Double = radius

  def getCenter: Point = center

  def getPath: Path = hex


  def redraw(scaleFactor: Double) = {
    //console log "draw hexagon with center at: " + center
    if (hex != null) hex remove()

    val c = center.multiply(scaleFactor)
    hex = Path RegularPolygon(c, 6, radius * scaleFactor)
    hex rotate 30

    //hex.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
    hex fillColor = color
    hex strokeColor = Color("#3c3f41")
    hex strokeWidth = 1
  }

  def assignNeighbours: Any = {
    //console.log("====== assignNeighbours on ", center.toString())
    // We assume this Hexagon is Visible
    for(i <- 0 to 5) {
      //console.log("=== checking neighbour " + i)
      neighbours(i) match {
        case PhantomHexagon => {
          val center = PhantomHexagon.getCenter(this, HexConstants.sideMappings(i))
          //console.log("creating new Hexagon at Pos", center.toString())
          val hex = Viewer.findOrCreateHexagon(center, radius, scaleFactor)
          neighbours(i) = hex
          hex match {
            case visibleHexagon: VisibleHexagon =>
              visibleHexagon.neighbours(HexConstants.sideMappings(i)) = this
              visibleHexagon.assignNeighbours
            case _ =>
          }
        }
        case x: VisibleHexagon  => { x.neighbours(HexConstants.sideMappings(i)) = this /*console.log("Neighbour " + i + " is a visible Hexagon")*/ }
      }
    }
  }
}
