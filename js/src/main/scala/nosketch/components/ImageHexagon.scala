package nosketch.components

import nosketch.viewport.ViewPort
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLImageElement
import paperjs.Basic.{Rect, Point, Size}
import paperjs.Items.Raster
import paperjs.Paths.Path
import paperjs.Projects.Project
import paperjs.Styling.Color
import paperjs.Typography.PointText


/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class ImageHexagon(center: Point, radius: Double, scaleFactor: Double) extends VisibleHexagon(center, radius, scaleFactor)  {

  var raster:Raster = null
  val bgImage = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
  //bgImage.src = "/assets/images/hex.png"
  bgImage.src = "/assets/shapes/" + (Math.random() * 15).round + ".svg"

  override def getRadius: Double = radius

  override def getCenter: Point = center

  override def redraw(scaleFactor: Double): Unit = {
    super.redraw(scaleFactor)
    layer.activate()

    val imageSize = new Size(CircleCanvas.getRadiusForInnerCircle(radius) * 2 * scaleFactor * 0.8)

    if(raster != null) {
      raster.remove()
    }
    raster = new Raster(bgImage, center.multiply(scaleFactor))
    raster.fitBounds(Rect(raster.position.x - imageSize.width / 4, raster.position.y - imageSize.height / 4, imageSize.width / 2, imageSize.height / 2))
//
//    val centerIndicator = Path.Circle(center, 20)
//    centerIndicator.fillColor = Color("#000000")
//    centerIndicator.strokeColor = Color("#ffffff")
//    centerIndicator.strokeWidth = 3
  }

  override def destroy: Unit = {
    super.destroy
    // implement destroy
  }

}
