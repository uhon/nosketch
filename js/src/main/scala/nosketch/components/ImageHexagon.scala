package nosketch.components

import nosketch.viewport.ViewPort
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLImageElement
import paperjs.Basic.Point
import paperjs.Items.Raster
import paperjs.Paths.Path
import paperjs.Projects.Project
import paperjs.Styling.Color
import paperjs.Typography.PointText
import paperjs.Basic.Size


/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class ImageHexagon(center: Point, radius: Double, scaleFactor: Double) extends VisibleHexagon(center, radius, scaleFactor)  {

  var raster:Raster = null
  val bgImage = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
  bgImage.src = "/assets/images/hex.png"

  override def getRadius: Double = radius

  override def getCenter: Point = center

  override def redraw(scaleFactor: Double): Unit = {
    layer.activate()

    val imageSize = new Size(CircleCanvas.getRadiusForInnerCircle(radius) * 2)

    if(raster != null) {
      raster.remove()
    }
    raster = new Raster(bgImage, center.multiply(scaleFactor))
    //raster.size = imageSize
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
