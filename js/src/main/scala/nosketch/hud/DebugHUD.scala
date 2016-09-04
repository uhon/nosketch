package nosketch.hud

import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Basic.Rect
import paperjs.Basic.Size
import nosketch.components.ZoomAwareObject
import paperjs.Items.{Layer, Item, Group, Shape}
import paperjs.Items.Shape.Rectangle
import paperjs.Styling.Color
import org.querki.jquery._
import scalatags.Text.all._
import scala.scalajs.js
import scala.scalajs.js.Object
import scala.scalajs.js
import scala.scalajs.js.Any
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._
import org.querki.jquery._
import vongrid._
import vongrid.lib._
import vongrid.config._
import org.denigma.threejs._
import vongrid.utils.{MC, MouseCaster, Scene}

import scala.scalajs.js.timers._
import js.Dynamic.{global => g}
import js.Dynamic.{literal => l}


/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object DebugHUD {
  def update = elements.foreach(_.update)


  var elements = List[DebugHUDElement]()

  def addElement(element: DebugHUDElement) = {
    elements :+= element
    render
  }


  def render = {
    $("#debugHUD").remove()
//    $("#magicContainer").append(
    $("body").append(
      div(id := "debugHUD",
        elements.map(_.render)
      ).toString()
    )
  }
}
