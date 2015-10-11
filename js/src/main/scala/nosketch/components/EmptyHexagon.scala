package nosketch.components

import paperjs.Basic.Point
import paperjs.Styling.Color
import paperjs.Typography.PointText

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class EmptyHexagon(center: Point, radius: Double, scaleFactor: Double) extends VisibleHexagon(center, radius, scaleFactor)  {

  val centerTextColor = Color(Math.random(), Math.random(), Math.random(), 1)
  var centerText: PointText = null

  override def getRadius: Double = radius

  override def getCenter: Point = center

  override def redraw(scaleFactor: Double): Unit = {
    super.redraw(scaleFactor)
    if(centerText != null) {
      centerText.remove()
    }
    centerText = new PointText(center.multiply(scaleFactor))
    centerText.fillColor = centerTextColor
    centerText.content = center.toString()
    centerText.fontSize = 24

  }

  override def destroy(): Unit = {
    super.destroy
    centerText.remove()
  }

}
