package nosketch.components

import nosketch.util.HexConstants
import paperjs.Basic.Point

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object PhantomHexagon extends AbstractHexagon{

  def getCenter(neighbour: VisibleHexagon, side: Int): Point = {
//    neighbours.find(_.isInstanceOf[VisibleHexagon]) match {
//      case None => new Point(0)
//      case x: VisibleHexagon => x.getCenter *
    val neighboursSide = HexConstants.sideMappings(side)
    val vectorFromNeighbour = new Point(CircleCanvas.getRadiusForInnerCircle(neighbour.getRadius),0)
    vectorFromNeighbour.angle = 30 + HexConstants.angleToHexagonOnSide(side)
    neighbour.getCenter.add(vectorFromNeighbour)
  }
}
