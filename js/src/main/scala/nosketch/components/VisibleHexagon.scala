package nosketch.components

import nosketch.Viewer3D
import org.scalajs.dom.console
import nosketch.components.PhantomHexagon
import nosketch.components.EmptyHexagon
import nosketch.util.HexConstants
import org.denigma.threejs.Vector3
import paperjs.Basic.Point
import paperjs.Paths.Path
import paperjs.Styling.Color
import paperjs.Items.{Item, Layer}
import vongrid.{AbstractCell, Cell}
import scala.scalajs.js.timers._

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
@ScalaJSDefined
abstract class VisibleHexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = 0d) extends Cell(q, r, s, h) {

  var neighbours: js.Array[_ >: Cell] = js.Array[Cell](PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon)


  def getCenter = grid.cellToPixel(this)
  def destroy: Any = {
    // TODO: REMOVE DATA

    for (i <- 0 to 5) {
      neighbours(i) match {
        case x: VisibleHexagon => x.neighbours(HexConstants.sideMappings(i)) = PhantomHexagon
        case PhantomHexagon =>
      }
    }
  }


  /**
   * Assign new hexagons as neighbour if they fit into the bounds of the current view
   * newly on screen
 *
   * @return
   */
  def assignNeighbours: Any = {
    //console.log("====== assignNeighbours")
    // We assume this Hexagon is Visible
    for(i <- 0 to 5) {
      //console.log("=== checking neighbour " + i)
      neighbours(i) match {
        case PhantomHexagon => {
          val neighbourCenter = PhantomHexagon.calculateCellAtCenter(this, grid.getDirection(i))
          console.log("Neighbour is a Phantom  with center", neighbourCenter)
          //console.log("creating new Hexagon at Pos", center.toString())
          // TODO: calc position of new hex by adding directions-cell
          val hexTuple = Viewer3D.findOrCreateHexagon(grid.cellToPixel(neighbourCenter))

          console.log("hexTUPLE", hexTuple.toString())

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
