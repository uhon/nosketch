package nosketch.components

import scala.scalajs.js.annotation.ScalaJSDefined


/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
@ScalaJSDefined
class EmptyHexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = 0d) extends VisibleHexagon(grid, q, r, s, h)  {


  override def destroy(): Unit = {
    super.destroy
  }

}
