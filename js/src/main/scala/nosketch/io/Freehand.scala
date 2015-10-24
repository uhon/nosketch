package nosketch.io

import java.awt.event.MouseEvent

import nosketch.components.Hexagon
import nosketch.controls.ControlHandler
import nosketch.util.{MouseEventListener, MouseEventDistributor}
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Items.{Group, Item}
import paperjs.Paths.Path
import paperjs.Styling.Color
import paperjs.Tools.ToolEvent
import paperjs._

import scala.scalajs.js
import scala.scalajs.js.Math

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class Freehand(hexagon: Hexagon, var scaleFactor: Double) extends MouseEventListener{
  var currentStrokeWidth = ControlHandler.StrokeWidth.Medium

  var currentPath: Path = null

  var dragActive = false

  MouseEventDistributor.registerToMouseEvents(this)

  override def onMouseUp(event: ToolEvent) = finishShape()

  override def onMouseDown(event: ToolEvent): Unit = {
    dragActive = true
    // If we produced a path before, deselect it:
    if (currentPath != null) {
      currentPath.selected = false
    }


    currentPath = Path()
    hexagon.addShape(currentPath)
    currentPath.removeSegment(0)
    // console.log(currentPath)
    currentPath.strokeWidth = currentStrokeWidth.toString.toDouble * scaleFactor
    currentPath.add(event.point)
    currentPath.strokeColor = Color("black")

    // Select the path, so we can see its segment points:
    currentPath.fullySelected = true
  }



  override def onMouseDrag(event: ToolEvent): Unit = {
    if (dragActive) {
      // Every drag event, add a point to the path at the current
      // position of the mouse:
      val x1 = event.point.x
      val y1 = event.point.y
      val x0 = Paper.view.center.x
      val y0 = Paper.view.center.y

      val distanceToCenter = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0))

      if (distanceToCenter < hexagon.getCircleCanvas.getRadius / 2 * scaleFactor) {
        currentPath.add(event.point)

//        if (hexagon.connectors != null) {
//          for (con <- hexagon.connectors) {
//            var test = con.getPath.hitTest(event.point, Dynamic.literal())
//
//            // TODO: ifHit
//            finishShape()
//          }
//        }
      }
    }
  }

  override def onMouseMove(event: ToolEvent) = {}

  override def onMouseScroll(event: WheelEvent) = {}

  def finishShape(): Unit = {
    dragActive = false
    // When the mouse is released, simplify it:
    currentPath.simplify()

    // console.log("path started at: " + currentPath.getPointAt(0, isParameter = false))
    // console.log("path ended at: " + currentPath.getPointAt(currentPath.length - 1, isParameter = false))

    // Select the path, so we can see its segments:
    currentPath.selected = true
  }



}