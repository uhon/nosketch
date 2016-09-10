package nosketch.components

import org.scalajs.dom

import scalajs.js.timers.setTimeout
import org.scalajs.dom.raw.{Event, HTMLImageElement}
import paperjs.Basic.{Point, Rect, Size}
import paperjs.Items.Raster
import vongrid.Cell

import scala.scalajs.js.annotation.ScalaJSDefined


/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
@ScalaJSDefined
class ImageHexagon(var grid: NSGrid, q: Double, r: Double, s: Double, h: Double = 0d) extends VisibleHexagon(grid, q, r, s, h)  {
  def this(grid: NSGrid) = this(grid, 0, 0, 0, 0)
  val bgImage = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
  //bgImage.src = "/assets/images/hex.png"
  bgImage.src = nosketch.io.ImageUrls.randomPngShape
  bgImage.addEventListener("load", { e: Event => draw })

  def draw = {
    // TODO: Draw Image as Mesh or Sprite
  }

  override def destroy: Unit = {
    super.destroy
    // implement destroy
  }

}
