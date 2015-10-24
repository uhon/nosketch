package nosketch.components

import nosketch.Viewer
import org.scalajs.dom.console
import nosketch.components.PhantomHexagon
import nosketch.components.EmptyHexagon
import nosketch.util.HexConstants
import paperjs.Basic.Point
import paperjs.Paths.Path
import paperjs.Styling.Color
import paperjs.Items.{Item, Layer}
import scala.scalajs.js

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
abstract class VisibleHexagon(center: Point, radius: Double, scaleFactor: Double, showInnerCircle: Boolean = false) extends AbstractHexagon {
  val layer = new Layer(new js.Array[Item](0))

  var neighbours: Array[_ >: AbstractHexagon] =  Array[AbstractHexagon](PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon)

  var hex: Path = null

  val color = Color(Math.random(), Math.random(), Math.random(), 0.3)

  def destroy: Any = {
    //layer.removeChildren()
    layer.remove()
    if(hex != null) {
      hex.remove
      hex = null
    }

    for (i <- 0 to 5) {
      neighbours(i) match {
        case x: VisibleHexagon => x.neighbours(HexConstants.sideMappings(i)) = PhantomHexagon
        case x: AbstractHexagon => {}
      }
    }
  }






  def getRadius: Double = radius

  def getCenter: Point = center

  def getPath: Path = hex


  def redraw(scaleFactor: Double) = {
    //console.log("redraw")
    layer.activate()
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

  /**
   * Assign new hexagons as neighbour if they fit into the bounds of the current view
   * newly on screen
   * @return
   */
  def assignNeighbours: Any = {
    //console.log("====== assignNeighbours")
    // We assume this Hexagon is Visible
    for(i <- 0 to 5) {
      //console.log("=== checking neighbour " + i)
      neighbours(i) match {
        case PhantomHexagon => {
          val center = PhantomHexagon.getCenter(this, HexConstants.sideMappings(i))
          //console.log("creating new Hexagon at Pos", center.toString())
          val hexTuple = Viewer.findOrCreateHexagon(center, radius, scaleFactor)
          neighbours(i) = hexTuple._1
          hexTuple match {
            case (v: VisibleHexagon, r:Boolean) =>
              v.neighbours(HexConstants.sideMappings(i)) = this
              // TODO:
              // val leftNeighbour = i -1  <== tell the new hexagon the two relevant neighbours
              if(r) {
                v.assignNeighbours
              }
            case _ =>
          }
        }
        case x: VisibleHexagon  => { x.neighbours(HexConstants.sideMappings(i)) = this /*console.log("Neighbour " + i + " is a visible Hexagon")*/ }
      }
    }
  }
}
