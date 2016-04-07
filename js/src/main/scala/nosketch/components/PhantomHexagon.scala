package nosketch.components

import org.denigma.threejs.Vector3
import vongrid.{ACell, Cell}
import org.scalajs.dom._

import scala.scalajs.js.annotation.ScalaJSDefined
;

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
@ScalaJSDefined
object PhantomHexagon extends Cell(q=0,r=0,s=0,h=0) {

  def calculateCenter(neighbour: VisibleHexagon, side: vongrid.Cell): Vector3 = {
    val centerCell = new Cell(0,0,0)
    centerCell.copy(neighbour)
    centerCell.add(side)

    new Vector3(centerCell.q, centerCell.r, centerCell.s)
  }
}
