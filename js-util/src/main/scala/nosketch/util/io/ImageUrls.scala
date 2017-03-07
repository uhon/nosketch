package nosketch.util.io

import nosketch.util.loading.FA

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object ImageUrls {
  val notFound = "/assets/shapes/notFound.png"

  def randomFA = FA(scala.util.Random.nextInt(729)).toString
  def pngShape(name: String) = s"/assets/font-awesome/black/png/256/$name.png"

//  def svgShape(name: String) = s"/assets/images/sphere.gif"
  def svgShape(name: String) = s"/assets/font-awesome/black/svg/$name.svg"

  def randomPngShape = pngShape(randomFA)

  def anchor = s"/assets/font-awesome/white/png/256/anchor.png"
  def android = s"/assets/font-awesome/white/png/256/android.png"
  def doodle = s"/assets/shapes/doodle.svg"

  // Attention, SVG freezes system wen load as sprites in masses!
  def randomSvgShape = svgShape(randomFA)
}
