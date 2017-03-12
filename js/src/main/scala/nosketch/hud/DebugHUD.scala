package nosketch.hud

import nosketch.viewport.ViewPort
import paperjs.Basic.Point
import paperjs.Basic.Rect
import paperjs.Basic.Size
import nosketch.components.ZoomAwareObject
import nosketch.hud.elements.debug.{IncrementalIndicator, SimpleIndicator, TextIndicator}
import paperjs.Items.{Group, Item, Layer, Shape}
import paperjs.Items.Shape.Rectangle
import paperjs.Styling.Color
import org.querki.jquery.{JQueryEventObject, _}
import scalatags.Text.all._
import scala.scalajs.js
import scala.scalajs.js.Object
import scala.scalajs.js
import scala.scalajs.js.Any
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom.{css => _, _ }
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
  var disabled = true



  def update = {
    if(!disabled)  elements.foreach(_.update)
  }

  def toggle(e: JQueryEventObject) = {
    if(disabled) {
      elements.foreach(_.disabled = false)
      $("#debugHUD .stats").fadeIn()
      disabled = false
    } else {
      elements.foreach(_.disabled = true)
      $("#debugHUD .stats").fadeOut()
      disabled = true
    }
  }



  var elements = List[DebugHUDElement]()

  def addElement(element: DebugHUDElement) = {
    elements :+= element
    render
  }

  // Default indicators
  val meshCreation = new SimpleIndicator("Δ mesh creation")
  val animationCycles = new IncrementalIndicator("# anim. cycles")
  val viewUpdates = new IncrementalIndicator("# world updates")
  val sceneUpdates = new IncrementalIndicator("# scene updates")
  val tileCreations = new IncrementalIndicator("# created tiles")
  val tileDisposes = new IncrementalIndicator("# disposed tiles")
  val cellCreations = new IncrementalIndicator("# created cells")
  val cellDisposes = new IncrementalIndicator("# disposed cells")
  val spriteDisposes = new IncrementalIndicator("# disposed sprites")
  val spriteShowCtrl = new IncrementalIndicator("# show controls")
  val spriteHideCtrl = new IncrementalIndicator("# hide controls")
  val texturesLoaded = new IncrementalIndicator("# Texture loaded")
  val texturesCached = new IncrementalIndicator("# Texture from cache")

  val cameraPosition = new SimpleIndicator("° camera position")
  val cameraRotation = new SimpleIndicator("° camera rotation")
  val activeCell = new SimpleIndicator("* active cell")
//  val ctrlOffset = new SimpleIndicator("offset")
//  val ctrldeltaY = new SimpleIndicator("deltaY")
//  val ctrlDistance = new SimpleIndicator("target distance")
//  val ctrlTarget = new SimpleIndicator("target")
//  val ctrlOffsetX = new SimpleIndicator("ctrlOffsetX")
//  val ctrlOffsetY = new SimpleIndicator("ctrlOffsetY")

  addDefaultElements

  def addDefaultElements = {
    addElement(viewUpdates)
    addElement(sceneUpdates)
    addElement(cellCreations)
    addElement(tileCreations)
    addElement(cellDisposes)
    addElement(tileDisposes)
    addElement(spriteDisposes)
//    addElement(spriteShowCtrl)
//    addElement(spriteHideCtrl)
    addElement(animationCycles)
//    addElement(texturesLoaded)
//    addElement(texturesCached)
    addElement(cameraPosition)
    addElement(cameraRotation)
    //    addElement(ctrlOffset)
    //    addElement(ctrldeltaY)
    //    addElement(ctrlDistance)
    //    addElement(ctrlTarget)
    //    addElement(ctrlOffsetX)
    //    addElement(ctrlOffsetY)
    addElement(activeCell)
    addElement(meshCreation)
  }


  def render = {
    $("#debugHUD").remove()
//    $("#magicContainer").append(
    $("body").append(
      div(id := "debugHUD",
        div(`class` := "row",
          div(`class` := "col-xs-12 toggler",
            i(`class` := "fa fa-bar-chart")
          ),
          div(`class` := "col-xs-12 stats", css("display") := "none",
            elements.map(_.render)
          )
        )
      ).toString()
    )

    $("#debugHUD .toggler").on("click", toggle _)
    $("#debugHUD .toggler").click()
  }

  def reset: Unit = {
    elements = List()
    addDefaultElements
  }
}
