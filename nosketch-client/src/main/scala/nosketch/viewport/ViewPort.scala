package nosketch.viewport

import nosketch.{SimplePanAndZoom, Playground}
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import paperjs.Basic._
import paperjs.Styling._
import paperjs.Typography.PointText
import paperjs._
import paperjs.Paper._

import scala.scalajs.js._
import org.scalajs.jquery._


class ViewPort(canvas: Canvas, playground: Playground) {

  var scaleIndicator: PointText = null

  val defaultPlaygroundSize = 500d

  val center = new Point(defaultPlaygroundSize / 2, defaultPlaygroundSize / 2)

  var scaleFactor = 1d




  def init = {
    Paper.setup(canvas)

    view.viewSize = new Size(
      defaultPlaygroundSize,
      defaultPlaygroundSize
    )

    window.onresize = (event: UIEvent) => {
      resizeWindow()
    }

    window.onmousewheel = (event: WheelEvent) => mouseScrolled(event)

    window.onkeydown = (event: KeyboardEvent) => {

      view.center = event.keyCode match {
        case 37 => SimplePanAndZoom.changeCenter(view.center, 1, 0, 100) // left
        case 38 => SimplePanAndZoom.changeCenter(view.center, 0, -1, 100) // up
        case 39 => SimplePanAndZoom.changeCenter(view.center, -1, 0, 100) // right
        case 40 => SimplePanAndZoom.changeCenter(view.center, 0, +1, 100 ) // down
      }

      playground.update
      view.update()


    }


    resizeWindow()
    view.update()
  }



  def mouseScrolled(event: WheelEvent) = {

    val topLeftCorner = jQuery("#canvas").offset().asInstanceOf[Dynamic]
    val cTop = topLeftCorner.selectDynamic("top").asInstanceOf[Double]
    val cLeft = topLeftCorner.selectDynamic("left").asInstanceOf[Double]

    val mousePosition = new Point(event.pageX - cLeft, event.pageY - cTop)

    console.log("mousePosition:", mousePosition)

    val zoomAndOffset = StableZoom.changeZoom(view.zoom, event.deltaY, view.center, mousePosition)
    view.zoom = zoomAndOffset._1
    val currentOffset = zoomAndOffset._2

    view.center = view.center add currentOffset



    // console.log("center after: ", center)

    // console.log("offset", offset)

    //playground.update
    view.update()
  }



  def calculateScaleFactor: Double = {

    val wWidth = view.size.width
    val wHeight = view.size.height // minus the padding that is applied. a possible headers should also be taken in place

    val zWidth = wWidth / defaultPlaygroundSize
    val zHeight = wHeight / defaultPlaygroundSize
    // console.log("windowWidth: " + wWidth + ", windowHeight: " + wHeight)


    val scaleFactor = if(zWidth < zHeight) zWidth else zHeight

    if(scaleIndicator != null) {
      scaleIndicator remove()
    }
    scaleIndicator = new PointText(Point(30,30));
    scaleIndicator.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
    scaleIndicator.content = scaleFactor.toString
    scaleIndicator.fontSize = 40

    scaleFactor
  }


  def resizeWindow() = {
    val newSize = if (window.innerHeight < window.innerWidth) window.innerHeight else window.innerWidth

//    val deltaWidth = view.viewSize.width - newSize
//    val deltaHeight = view.viewSize.height - newSize


   // view.center = view.center.add(new Point(deltaWidth, deltaHeight))
    view.viewSize = new Size(
      newSize - 100,
      newSize - 100
    )


    scaleFactor = calculateScaleFactor

    playground.update
    view.update()
  }




}