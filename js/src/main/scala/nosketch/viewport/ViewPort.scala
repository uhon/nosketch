package nosketch.viewport

import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.{FPSIndicator, MouseIndicator}
import nosketch.io.{TouchEventListener, TouchEventDistributor, MouseEventListener, MouseEventDistributor}
import nosketch.util.Profiler._
import nosketch.{Viewer, SimplePanAndZoom, ViewportSubscriber}
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import paperjs.Basic._
import paperjs.Items.Raster
import paperjs.Projects.{View, FrameEvent}
import paperjs.Styling._
import paperjs.Projects.Project
import paperjs.Tools.ToolEvent
import paperjs.Typography.PointText
import paperjs._
import paperjs.Paper._

import scala.scalajs.js._
import org.scalajs.jquery._


class ViewPort(canvas: Canvas, playground: ViewportSubscriber, squared: Boolean = false, allowPanAndZoom: Boolean = true) extends MouseEventListener with TouchEventListener {
  val defaultPlaygroundSize = 500d

  val center = new Point(defaultPlaygroundSize / 2, defaultPlaygroundSize / 2)

  var scaleFactor = 1d

  var activeSlideAction: Option[SlideAction] = None

  var activeDragAction: Option[DragAction] = None

  var activeZoomAction: Option[ZoomAction] = None


  def getView = view

  MouseEventDistributor.registerToMouseEvents(this)
  TouchEventDistributor.registerTouchListener(canvas, this, this)


  def getOffsetVector = {
    view.center.subtract(center)
  }


  def init = {
    Paper.setup(canvas)
    view.viewSize = new Size(
      defaultPlaygroundSize,
      defaultPlaygroundSize
    )

    window.onresize = (event: UIEvent) => {
      resizeWindow
    }

    window.onkeydown = (event: KeyboardEvent) => {
      if(event.keyCode >= 37 && event.keyCode <= 40) {
//        console.log("on key down")
        if(activeSlideAction.isEmpty) {
          activeSlideAction = Some(SlideAction(event))
        } else {
          activeSlideAction.get.event = event
        }
      }

      //playground.onScale

      //view.update()
    }

    window.onkeyup = (event: KeyboardEvent) => {
      if(event.keyCode >= 37 && event.keyCode <= 40) {
        activeSlideAction = None
      }
    }

    // TODO: this might be an option for performance improvement. use views onFrame method
    view.onFrame = (v: View, e: FrameEvent) => onFrameEvent(v, e)

  }

  def onFrameEvent(v: View, e: FrameEvent) = {
    handleSlides
    handleDrags
    handleZoom

    if(e.count % 5 == 0) FPSIndicator.fps = calcFPS(e)
      DebugHUD.redraw(this)
  }

  def handleSlides = {
    activeSlideAction match {
      case Some(x) => {
        val speedFactor = 0.8
        val now = Date.now()
        val delta = (now - x.startTime) * speedFactor
        x.startTime = now

        //console.log(delta)

        view.center = x.event.keyCode match {
          case 37 => SimplePanAndZoom.changeCenter(view.center, -1, 0, delta) // right
          case 38 => SimplePanAndZoom.changeCenter(view.center, 0, +1, delta) // down
          case 39 => SimplePanAndZoom.changeCenter(view.center, 1, 0, delta) // left
          case 40 => SimplePanAndZoom.changeCenter(view.center, 0, -1, delta) // up
        }

        playground.onZoom

      }
      case None => Unit
    }
  }

  def handleDrags = {
    activeDragAction match {
      case Some(x) => {
        val eventPoint = x.eventPoint
        val vector = x.initialMouseCoordinates.subtract(eventPoint.subtract(x.initialViewCenter.subtract(view.center)))
//        console.log("vector on drag", vector)
        val newCenter = SimplePanAndZoom.changeCenter(x.initialViewCenter,  vector.x, vector.y * -1, 1 / view.zoom) // down


        if(!view.center.equals(newCenter)) {
          view.center = newCenter
          //console.log("move center to", newCenter)
          x.initialMouseCoordinates = eventPoint
          x.initialViewCenter = newCenter

          playground.onZoom
        }
      }
      case None => Unit
    }
  }



  def handleZoom() = {
    // TODO: only works for viewer at the moment, drawer can't zoom. must be implemented
    if(jQuery("#canvas").length > 0) {
      activeZoomAction match {
        case Some(x) => {
          val topLeftCorner = jQuery("#canvas").offset().asInstanceOf[Dynamic]
          val cTop = topLeftCorner.selectDynamic("top").asInstanceOf[Double]
          val cLeft = topLeftCorner.selectDynamic("left").asInstanceOf[Double]

          //val mousePosition = new Point(event.pageX - cLeft - view.size.width / 2, event.pageY - cTop - view.size.height / 2).add(view.center)
          val mousePosition = MouseEventDistributor.currentMousePosition
          console.log("mousePosition on scroll", mousePosition)

          val startZoomAndOffset = System.nanoTime()
          val zoomAndOffset = StableZoom.changeZoom(view.zoom, x.delta, view.center, mousePosition)
          activeZoomAction = None
          reportDuration("zoom and offset calc", startZoomAndOffset)

          val startZoomView = System.nanoTime()
          view.zoom = zoomAndOffset._1
          //playground.onZoom
          reportDuration("zoom the PaperJs-View", startZoomView)

          val currentOffset = zoomAndOffset._2

          val startOffsetTime = System.nanoTime()
          view.center = view.center add currentOffset
          reportDuration("Viewer::offset the PaperJs-View", startOffsetTime)

          playground.onZoom
        }
        case None => Unit
      }
    }
  }



  def calculateScaleFactor: Double = {

    val wWidth = view.size.width
    val wHeight = view.size.height // minus the padding that is applied. a possible headers should also be taken in place

    val zWidth = wWidth / defaultPlaygroundSize
    val zHeight = wHeight / defaultPlaygroundSize
    // console.log("windowWidth: " + wWidth + ", windowHeight: " + wHeight)


    val scaleFactor = if(zWidth < zHeight) zWidth else zHeight

    scaleFactor
  }


  def resizeWindow = {

    if(squared) {
      val newSize = if (window.innerHeight < window.innerWidth) window.innerHeight else window.innerWidth

      //    val deltaWidth = view.viewSize.width - newSize
      //    val deltaHeight = view.viewSize.height - newSize


      // view.center = view.center.add(new Point(deltaWidth, deltaHeight))
      view.viewSize = new Size(
        newSize,
        newSize
      )
    } else {
      view.viewSize = new Size(
        window.innerWidth,
        window.innerHeight

      )
    }


    scaleFactor = calculateScaleFactor

    playground.onScale

    TouchEventDistributor.reRegisterObservers(this)
  }

  def cornerTopLeft() = new Point(view.bounds.left, view.bounds.top)
  def cornerTopRight() = new Point(view.bounds.left + view.bounds.width, view.bounds.top)
  def cornerBottomLeft() = new Point(view.bounds.left, view.bounds.top + view.bounds.width)
  def cornerBottomRight() = new Point(view.bounds.left + view.bounds.width, view.bounds.top + view.bounds.height)


  override def onRealMouseDrag(event: MouseEvent) = {
    onDrag(new Point(event.clientX, event.clientY))
  }

  override def onDrag(eventPoint: Point) = {
//    console.log("drag on: ", eventPoint)
    if(allowPanAndZoom) {
      if(activeDragAction.isEmpty) {
        activeDragAction = Some(DragAction(eventPoint, eventPoint, view.center))
      } else {
        activeDragAction.get.eventPoint = eventPoint
      }
    }
  }


  override def onMouseScroll(event: WheelEvent) = onMouseScrollFirefox(event.deltaY)

  override def onMouseScrollFirefox(deltaY: Double) = {
    if(allowPanAndZoom) {
      if(activeZoomAction.isEmpty) {
        activeZoomAction = Some(ZoomAction(deltaY, MouseEventDistributor.currentMousePosition))
      } else {
        activeZoomAction.get.delta += deltaY
      }
    }
  }

  override def onZoom(delta: Double, touchCenter: Point) = {
    if(allowPanAndZoom) {
      if(activeZoomAction.isEmpty) {
        activeZoomAction = Some(ZoomAction(delta, MouseEventDistributor.currentMousePosition))
      } else {
          activeZoomAction.get.delta += delta
      }
    }
  }



  override def onMouseUp(event: ToolEvent) = {
    activeDragAction = None
  }

  def calcFPS(event: FrameEvent) = {
    (1/event.delta).toInt
  }

}