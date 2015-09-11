package nosketch

import org.scalajs.dom._
import paperjs.Typography.PointText
import scala.scalajs.js._
import scala.scalajs.js.annotation.JSExport
import paperjs._
import Basic._,Paths._,Styling._,Tools._

@JSExport
object Drawer {
  var defaultPlaygroundSize = 1000d

  var hexagon: Hexagon = null

  var freeHand: Freehand = null

  var scaleIndicator: PointText = null
  

  @JSExport
  def startDrawer(canvas: html.Canvas): Unit = {
    Paper.setup(canvas)

    Paper.view.viewSize = new Size(
      defaultPlaygroundSize,
      defaultPlaygroundSize
    )

    // Calculate initial ZoomFactor
    val scaleFactor = calculateZoomFactor

    val centerOfCanvas = new Point(defaultPlaygroundSize / 2, defaultPlaygroundSize / 2)

    hexagon = new Hexagon(centerOfCanvas, defaultPlaygroundSize / 2, scaleFactor)

    ControlHandler(freeHand)

    freeHand = new Freehand(hexagon, scaleFactor)


    val tool = Tool()

    tool.onMouseDrag = (event: ToolEvent) => freeHand.mouseDrag(event)
    tool.onMouseDown = (event: ToolEvent) => freeHand.mouseDown(event)
    tool.onMouseUp = (event: ToolEvent) => freeHand.finishShape()

    window.onresize = (event: UIEvent) => {
      resizeWindow()
      console.log ("view size: " + Paper.view.size)
      console.log ("view viewSize: " + Paper.view.viewSize)
      console.log ("view center: " + Paper.view.center)

    }

    resizeWindow()
  }


 def calculateZoomFactor: Double = {
   val wWidth = Paper.view.size.width
   val wHeight = Paper.view.size.height // minus the padding that is applied. a possible headers should also be taken in place
   console.log("windowWidth: " + wWidth + ", windowHeight: " + wHeight)


   val zWidth = wWidth / defaultPlaygroundSize
   val zHeight = wHeight / defaultPlaygroundSize
   var scaleFactor = 1d
   if(zWidth < zHeight) {
     scaleFactor = zWidth
   } else {
     scaleFactor = zHeight
   }

   if(scaleIndicator != null) {
     scaleIndicator.remove()
   }
   scaleIndicator = new PointText(Point(100,100));
   scaleIndicator.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
   scaleIndicator.content = scaleFactor.toString
   scaleIndicator.fontSize = 40

   scaleFactor
 }

  def resizeWindow() = {
    console.log("resize window...")

    val newWidth = if (window.innerHeight < window.innerWidth) window.innerHeight else window.innerWidth

    Paper.view.viewSize = new Size(
      newWidth,
      newWidth
    )

    val scaleFactor = calculateZoomFactor
    console.log("newZoomFactor: " + scaleFactor)

    this.hexagon.redraw(scaleFactor)

    freeHand.redraw(scaleFactor)

    Paper.view.update()
  }
}