package nosketch

import nosketch.ControlHandler.StrokeWidth
import org.scalajs.core.tools.logging.NullLogger
import org.scalajs.dom._
import org.scalajs.dom.html._
import org.scalajs.jquery._
import paperjs.Items.Shape
import scala.scalajs.js._
import scala.scalajs.js.annotation.JSExport
import paperjs._
import paperjs.Paths._
import rx._
import Basic._,Paths._,Styling._,Tools._

@JSExport
object Drawer {
  ControlHandler init()

  var wWidth = 1000
  var wHeight = 1000
  var cSize = 1000
  var defaultPlaygroundSize = 500
  var globalZoomFactor = 1
  var currentStrokeWidth = StrokeWidth.Medium
  var currentPath: Path = null
  var hexagon: Path = null
  var innerCircle: Path = null
  var innerCircleRadius: Double = 0.0
  var connectors = List[Path]()
  // Containing Paths
  var shapes = List[Path]()
  var dragActive = false
  
  val logger = NullLogger


  @JSExport
  def startDrawer(canvas: html.Canvas): Unit = {

    Paper.setup(canvas)
    val tool = Tool()

    //tool.minDistance = 1



    tool.onMouseDrag = (event: ToolEvent) => {
      if (dragActive) {
        // Every drag event, add a point to the path at the current
        // position of the mouse:
        var x1 = event.point.x
        var y1 = event.point.y
        var x0 = Paper.view.center.x
        var y0 = Paper.view.center.y
        var distanceToCenter = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0))
        if (distanceToCenter < this.innerCircleRadius) {
          currentPath.addPoint(event.point)

          for (con <- connectors) {
            var test = con.hitTest(event.point, Dynamic.literal())

            // TODO: ifHit
            //finishShape()
          }
        }
      }
    }

    tool.onMouseDown = (event: ToolEvent) => {
      dragActive = true
      // If we produced a path before, deselect it:
      if (currentPath != null) {
        currentPath.selected = false
      }

      currentPath = Path()
      currentPath.removeSegment(0)
      console.log(currentPath)
      currentPath.addPoint(event.point)
      currentPath.strokeColor = Color("black")
      currentPath.strokeWidth = currentStrokeWidth.toString.toInt

      // Select the path, so we can see its segment points:
      currentPath.fullySelected = true
    }

    tool.onMouseUp = (event: ToolEvent) => {
      finishShape()
    }

    window.onresize = (event: UIEvent) => {
      resizeWindow()
      console.log ("view size: " + Paper.view.size)
      console.log ("view viewSize: " + Paper.view.viewSize)
      console.log ("view center: " + Paper.view.center)

      console.log ("view size: " + Paper.view.size)
      console.log ("view viewSize: " + Paper.view.viewSize)
      console.log ("view center: " + Paper.view.center)
    }

    resizeWindow()
  }






  def resizeWindow() = {
    console.log("resize window...")
    //$("#drawing").width(defaultPlaygroundSize)
    //$("#drawing").height(defaultPlaygroundSize)

    wWidth = window.innerWidth
    wHeight = window.innerHeight -40 // minus the padding that is applied. a possible headers should also be taken in place
    console.log("windowWidth: " + wWidth + ", windowHeight: " + wHeight)
    console.log("oldZoomFactor: " + globalZoomFactor)

    var zWidth = wWidth / defaultPlaygroundSize
    var zHeight = wHeight / defaultPlaygroundSize
    this.cSize = 0
    var newZoomFactor = 1
    if(zWidth < zHeight) {
      cSize = wWidth
      newZoomFactor = zWidth
    } else {
      cSize = wHeight
      newZoomFactor = zHeight
    }
    console.log("newZoomFactor: " + newZoomFactor)

    currentStrokeWidth *= newZoomFactor / globalZoomFactor

    jQuery("#canvasContainer").height(cSize)
    jQuery("#canvasContainer").width(cSize + 200)
    jQuery("#drawing").width(cSize)
    jQuery("#drawing").height(cSize)

    Paper.view.viewSize = new Size(cSize, cSize)

    rescaleShapes(newZoomFactor / globalZoomFactor)
    redrawHexagon()
    redrawInnerCircle()
    redrawConnectors()
    globalZoomFactor = newZoomFactor
  }

  def rescaleShapes(zoomFactor: Double) = {
    var exportedJson = ""
    for(shape <- shapes) {
      shape.scaleAll(zoomFactor)
      shape.position *= zoomFactor
      shape.strokeWidth *= zoomFactor
      exportedJson += shape.exportJSON()
    }
    console.log(exportedJson)
  }

  def redrawHexagon() = {
    var mostRightCorner = Point(cSize / 2, cSize / 2)
    console.log("draw hexagon with rightCorner: " + mostRightCorner)
    if(hexagon != null) {
      // TODO: FIX CLEARING
      //hexagon.clear()
    }
    hexagon = Path.RegularPolygon(mostRightCorner, 6, cSize / 2)
    hexagon.fillColor = Color("#e9e9ff")
  }

  def redrawInnerCircle() = {
    if(innerCircle != null) {// TODO: FIX CLEARING
      innerCircle = null
    }
    console.log("cSize", cSize  )
    innerCircleRadius = 0.5 * Math.sqrt(3) * cSize / 2
    val center = Point(cSize / 2)
    console.log("redraw innerCircle with radius: " + innerCircleRadius + ", center: " + center)
    innerCircle = Path.Circle(center, innerCircleRadius)
    innerCircle.fillColor = Color("#e9e9aa")
  }

  def redrawConnectors () = {
    val vector = Point(0) + Point(innerCircleRadius, 0)
    console.log("initial vector: " + vector)
    for(i <- 0 to 5) {
      val center = Point(cSize / 2) + vector
      console.log("draw connector at center: " + center)
      val connector = Path.Circle(center, innerCircleRadius / 30)
      //connector = connector.center = vector
      connector.fillColor = Color("#336699")
      vector.angle += 60
      connectors = connectors.:+(connector)
    }
  }

  def finishShape() = {
    dragActive = false
    // When the mouse is released, simplify it:
    currentPath.simplify()

    console.log("path started at: " + currentPath.getPointAt(0, isParameter = false))
    console.log("path ended at: " + currentPath.getPointAt(currentPath.length - 1, isParameter = false))

    // Select the path, so we can see its segments:
    currentPath.selected = true

    shapes = shapes.:+(currentPath)
  }



}