package nosketch.util

import java.lang

import vongrid.utils.Tools
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.UndefOr
/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object NSTools {

  def randomizeRGBDouble(r: Int, g: Int, b: Int, range:Int): Double = {
    val rgb = List(r, g, b)
    val delta = randomInt(range)

    val colorString = rgb.map((p) => {
      val p1 = p + delta
      val pCalc = if (p1 < 0) 0
      else if (p1 > 255) 255
      else p1
      pCalc
    }).map((p) => {
      f"$p%02X"
    }).mkString("")

    Integer.parseInt(colorString, 16).toDouble
  }

  def generateID(): String = Tools.generateID()

  /**
    * If one value is passed, it will return something from -val to val.
    * Else it returns a value between the range specified by min, max.
    */
  def random(min: Int, max: UndefOr[Int]): Float = Tools.random(min, max)

  // from min to (and including) max
  def randomInt(min: Int, max: Int): Int = Tools.randomInt(min, max)
  def randomInt(min: Int): Int = Tools.randomInt(min)

  def normalize(v: Float, min: Int, max: Int): Float = Tools.normalize(v, min, max)

  def getShortRotation(angle: Float): Float = Tools.getShortRotation(angle)

  def isPlainObject(obj: js.Object): Boolean = Tools.isPlainObject(obj)

  // https://github.com/KyleAMathews/deepmerge/blob/master/index.js
  def merge(target: js.Object, src: js.Object): js.Object = Tools.merge(target, src)

  def getJSON(config: js.Object): String = Tools.getJSON(config)

}
