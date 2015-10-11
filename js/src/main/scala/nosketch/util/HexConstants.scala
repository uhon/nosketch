package nosketch.util

import nosketch.components.VisibleHexagon
import paperjs.Basic.Point

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object HexConstants {
  val angleToHexagonOnSide = Array(0, 60, 120, 180, 240, 300)
  val sideMappings = Array(3, 4, 5, 0, 1, 2) // maps sides of attached hexagon. A hexagon with a neighbour at side x is itself neighbour of the other on sideMappings(x)

}
