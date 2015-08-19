package sketcher

import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import paperjs._
import rx._
import Basic._,Paths._,Styling._,Tools._

@JSExport
object Sketcher {
  @JSExport
  def startSketchpad(canvas: html.Canvas): Unit = {

    Paper.setup(canvas)
    val tool = Tool()

    val width, height = Var(0.0)
    val center = Paper.view.center
    val points = 10
    val smooth = Var(true)
    val path = Path()

    val mousePos = Var(Paper.view.center / 2)
    val heightBuff = Var(center.y)
    val pathHeight = Rx {
      heightBuff() + (center.y - mousePos().y - heightBuff()) / 10
    }
    val eventCount = Var(0)

    path.fillColor = Color("blue")

    initializePath()

    def initializePath() = {
      width() = Paper.view.size.width
      height() = Paper.view.size.height / 2
      path.segments = js.Array[Segment]()
      path.addPoint(Paper.view.bounds.bottomLeft)
      for (i <- 1 to points - 1) {
        val point = Point(width() / points * i, center.y)
        path.addPoint(point)
      }
      path.addPoint(Paper.view.bounds.bottomRight)
      path.fullySelected = true
    }

    Obs(eventCount) {
      val pH = pathHeight()
      heightBuff() = pH
      for (i <- 1 to points - 1) {
        val sinSeed = eventCount() + i * 200
        val sinHeight = js.Math.sin(sinSeed / 200) * pathHeight()
        val yPos = js.Math.sin(sinSeed / 100) * sinHeight + height()
        path.segments(i).point.y = yPos
      }
      if (smooth()) path.smooth()
    }

    Obs(smooth) {
      if (!smooth()) {
        for (i <- 0 to path.segments.length - 1) {
          val segment = path.segments(i)
          segment.handleIn = Point.nullPoint
          segment.handleOut = Point.nullPoint
        }
      }
    }

    Paper.view.onFrame = (event: FrameEvent) => {
      eventCount() = event.count
    }

    tool.onMouseMove = (event: ToolEvent) => {
      mousePos() = event.point
    }

    tool.onMouseDown = (event: ToolEvent) => {
      val sm = smooth()
      smooth() = !sm
    }

    Paper.view.onResize = (event: FrameEvent) => {
      initializePath()
    }
  }
}