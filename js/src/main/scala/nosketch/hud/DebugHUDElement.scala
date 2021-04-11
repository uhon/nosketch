package nosketch.hud

import nosketch.components.ZoomAwareObject
import nosketch.util.NSTools
import org.denigma.threejs.{Euler, Vector2, Vector3, Vector4}
import paperjs.Basic.Point
import paperjs.Items.{Item, Layer}
import vongrid.utils.Tools
import org.querki.jquery._
import org.scalajs.dom.svg.Matrix

import scala.annotation.tailrec
import scala.scalajs.js
/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait DebugHUDElement {
  def update: Unit

  var disabled = true

  protected val key = ""
  protected var value = ""

  private val uid = NSTools.generateID()


  def render = s"""
        <div id="$uid" class="row indicator">
            <div class="container col-xs-12">
                <div class="row">
                    <div class="key col-xs-7"/>
                    <div class="value col-xs-5"/>
                </div>
            </div>
        </div>
    """


  def setValue(newValue: String): Unit = {
    value = newValue
    if(!disabled) {
      $(s"#$uid .value").html(value)
    }
  }

  def setValue(obj: js.Any): Unit = {
    setValue(
      (obj: Any) match {
        case p: Point => s"${prettifyNumber(p.x)},${prettifyNumber(p.x)}"
        case s:String => s
        case i:Int => prettifyNumber(i)
        case d:Double => prettifyNumber(d, 4)
        case v:Vector2 => v.toArray().map(prettifyNumber(_)).mkString("<br>")
        case v:Vector3 => v.toArray().map(prettifyNumber(_)).mkString("<br>")
        case v:Vector4 => v.toArray().map(prettifyNumber(_)).mkString("<br>")
        case e:Euler => List(e.x, e.y, e.z).map(prettifyNumber(_)).mkString("<br>")
        case a:Any => a.toString
        case _ => "unknown entity"
      }
    )
  }

  def prettifyNumber(d: Double, figAfterPoint: Int = 2): String = {
    val s = d.toString
    val pointIndex = s.indexOf(".")

    val suffix = if(pointIndex < 0) "" else s.substring(s.indexOf(".")).take(figAfterPoint + 1)
    val base = Math.abs(d).floor
    val sign = if(d >= 0) "" else "-"
    sign + prettifyNumber(base.toInt) + suffix
  }

  def prettifyNumber(i: Int): String = {
    @tailrec
    def prettyRec(s: String, acc: String): String = {
      if(s.isEmpty) return acc
      if(s.length % 3 == 0 && acc.nonEmpty) prettyRec(s.tail, acc + "'" + s.head)
      else prettyRec(s.tail, acc + s.head)
    }

    val sign = if(i >= 0) "" else "-"

    sign + prettyRec(Math.abs(i).toString, "")
  }
}
