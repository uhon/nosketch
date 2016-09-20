package nosketch.hud

import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Basic.Rect
import paperjs.Basic.Size
import nosketch.components.ZoomAwareObject
import nosketch.hud.elements.debug.{IncrementalIndicator, TextIndicator}
import paperjs.Items.{Group, Item, Layer, Shape}
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

  // Default indicators
  val updateView = new IncrementalIndicator("update view")
  val requestForUpdate = new IncrementalIndicator("request view update")
  val tileCreations = new IncrementalIndicator("created tiles")
  val tileDisposes = new IncrementalIndicator("disposedasfdsfasf tiles")
  val cellCreations = new IncrementalIndicator("created cells")
  val cellDisposes = new IncrementalIndicator("dissddfdsfposed cells")
  val spriteDisposes = new IncrementalIndicator("disposed sprites")
  val spriteShowCtrl = new IncrementalIndicator("show controls")
  val spriteHideCtrl = new IncrementalIndicator("hide controls")
  val texturesLoaded = new IncrementalIndicator("Texture loaded")
  val texturesCached = new IncrementalIndicator("Texture from cache")

  addDefaultElements

  def addDefaultElements = {
    addElement(updateView)
    addElement(requestForUpdate)
    addElement(tileCreations)
    addElement(tileDisposes)
    addElement(cellCreations)
    addElement(cellDisposes)
    addElement(spriteDisposes)
    addElement(spriteShowCtrl)
    addElement(spriteHideCtrl)
    addElement(texturesLoaded)
    addElement(texturesCached)
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

  def reset: Unit = {
    elements = List()
    addDefaultElements
  }
}
