package nosketch

import nosketch.ControlHandler.StrokeWidth
import paperjs.Basic.Point
import paperjs.Items.{Item, Group}
import paperjs.Paths.Path
import org.scalajs.dom._
import paperjs.Tools.ToolEvent
import paperjs._
import paperjs.Styling.Color

import scala.scalajs.js
import scala.scalajs.js.{Dynamic, Math}
import nosketch.ControlHandler

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class Freehand(hexagon: Hexagon, scaleFactor: Double) {


  // Containing Paths
  var shapes = List[Path]()

  var currentStrokeWidth = ControlHandler.StrokeWidth.Medium
  var currentPath: Path = null
  var oldScaleFactor = scaleFactor

  var group = Group(js.Array[Item]())

  var dragActive = false

  redraw(scaleFactor)


  def redraw(scaleFactor: Double) = {
    var exportedJson = ""
    group.removeChildren()
    group.remove()
    group = Group(js.Array[Item]())

    for (shape <- shapes) {
      shape.scaleAll(scaleFactor / oldScaleFactor)

      // TODO: There must be an easier way
      shape.position = new Point(
        shape.position.x / oldScaleFactor * scaleFactor,
        shape.position.y / oldScaleFactor * scaleFactor
      )
      shape.strokeWidth = shape.strokeWidth / oldScaleFactor * scaleFactor

      group.addChild(shape)
      exportedJson += shape.exportJSON()
    }

    oldScaleFactor = scaleFactor

    console.log(exportedJson)
  }

  def mouseDown(event: ToolEvent): Unit = {
    dragActive = true
    // If we produced a path before, deselect it:
    if (currentPath != null) {
      currentPath.selected = false
    }

    currentPath = Path()
    currentPath.removeSegment(0)
    console.log(currentPath)
    currentPath.strokeWidth = currentStrokeWidth.toString.toDouble * scaleFactor
    currentPath.addPoint(event.point)
    currentPath.strokeColor = Color("black")

    // Select the path, so we can see its segment points:
    currentPath.fullySelected = true
  }

  def mouseDrag(event: ToolEvent): Unit = {
    if (dragActive) {
      // Every drag event, add a point to the path at the current
      // position of the mouse:
      val x1 = event.point.x
      val y1 = event.point.y
      val x0 = Paper.view.center.x
      val y0 = Paper.view.center.y

      val distanceToCenter = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0))

      if (distanceToCenter < hexagon.getRadius) {
        currentPath.addPoint(event.point)

        //        for (con <- connectors) {
        //          var test = con.getPath.hitTest(event.point, Dynamic.literal())
        //
        //          // TODO: ifHit
        //          //finishShape()
        //        }
      }
    }
  }



  def finishShape(): Unit = {
    dragActive = false
    // When the mouse is released, simplify it:
    currentPath.simplify()

    console.log("path started at: " + currentPath.getPointAt(0, isParameter = false))
    console.log("path ended at: " + currentPath.getPointAt(currentPath.length - 1, isParameter = false))

    // Select the path, so we can see its segments:
    currentPath.selected = true

    shapes = shapes.:+(currentPath)
    group.addChild(currentPath)
  }

}