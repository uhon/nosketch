package nosketch

import nosketch.components.Hexagon
import nosketch.controls.ControlHandler
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{MouseIndicator, TextIndicator}
import nosketch.io.Freehand
import nosketch.viewport.ViewPort
import org.scalajs.dom._
import paperjs.Typography.PointText
import scala.scalajs.js._
import scala.scalajs.js.annotation.JSExport
import paperjs._
import Basic._,Paths._,Styling._,Tools._

@JSExport
object Drawer extends ViewportSubscriber {

  var hexagon: Hexagon = null

  var freeHand: Freehand = null

  var viewPort: ViewPort= null
  

  @JSExport
  def startDrawer(canvas: html.Canvas): Unit = {

    viewPort = new ViewPort(canvas, this)
    viewPort.init

    hexagon = new Hexagon(viewPort.center, viewPort.defaultPlaygroundSize / 2, viewPort.scaleFactor, true)

    freeHand = new Freehand(hexagon, viewPort.scaleFactor)
    ControlHandler(freeHand)

    DebugHUD.enabled = true
    DebugHUD.addElement(new TextIndicator(() => "scale: " + viewPort.scaleFactor.toString))
    DebugHUD.addElement(new TextIndicator(() => "zoom: " + viewPort.getView.zoom.toString))
    DebugHUD.addElement(new TextIndicator(() => "delta-c: " + viewPort.getOffsetVector))
    DebugHUD.addElement(new MouseIndicator(viewPort))

    viewPort.resizeWindow() // triggers on scale
    Paper.view.draw()
  }

  override def onScale = {
    if (viewPort != null) {
      if (hexagon != null) {
        hexagon.redraw(viewPort.scaleFactor)
        freeHand.scaleFactor = viewPort.scaleFactor
      }
      DebugHUD.redraw(viewPort)
    }
  }

  def onZoom = {
    if (viewPort != null) {
      DebugHUD.redraw(viewPort)
    }
  }
}