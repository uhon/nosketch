package nosketch.util.io

import nosketch.util.loading.FA

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object ImageUrls {
  val notFound = "/assets/shapes/notFound.png"

  def randomFA = FA(scala.util.Random.nextInt(729)).toString
  def pngShape(name: String) = s"/assets/font-awesome/white/png/256/anchor.png"

//  def svgShape(name: String) = s"/assets/images/sphere.gif"
  def svgShape(name: String) = s"/assets/font-awesome/white/svg/battery-3.svg"

  def randomPngShape = pngShape(randomFA)

  // Attention, SVG freezes system wen load as sprites in masses!
  def randomSvgShape = svgShape(randomFA)
}
