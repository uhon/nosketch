package nosketch.components

import nosketch.components.PathObject
import org.scalajs.dom._
import paperjs.Basic.Point
import paperjs.Items.{Item, Group}
import paperjs.Paths.Path
import paperjs.Styling.Color

import scala.scalajs.js

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class Hexagon(center: Point, radius: Double, var scaleFactor: Double, showInnerCircle: Boolean = false) extends PathObject {



  var hex: Path = null
  var shapes = List[Path]()
  var circleCanvas: CircleCanvas = null
  var connectors: List[CircleConnector] = List()
  var oldScaleFactor = scaleFactor
  var shapesGroup = Group(js.Array[Item]())




  // Setup circleCanvas
  circleCanvas = if(showInnerCircle) new CircleCanvas(this, scaleFactor) else null

  // Create Connectors
  for(i <- 0 to 5) {
    connectors = connectors :+ new CircleConnector(this, i, scaleFactor)
  }


  def addShape(theNewShape: Path): Any = {
    shapes :+= theNewShape
    shapesGroup.addChild(theNewShape)
  }



  def redraw(scaleFactor: Double) = {
    oldScaleFactor = this.scaleFactor
    this.scaleFactor = scaleFactor


    //console log "draw hexagon with center at: " + center
    if (hex != null) hex remove()

    val c = new Point(center.x * scaleFactor, center.y * scaleFactor)
    hex = Path RegularPolygon(c, 6, radius * scaleFactor)
    hex rotate 30

    //hex.fillColor = Color(Math.random(), Math.random(), Math.random(), 1)
    hex fillColor = Color("#f9f7ef")
    hex strokeColor = Color("#3c3f41")
    hex strokeWidth = 0.1

    if(showInnerCircle) circleCanvas redraw (scaleFactor)

    for(c <- connectors) {
      c redraw (scaleFactor)
    }


    redrawShapes(scaleFactor)

  }

  def getCircleCanvas = circleCanvas

  def getRadius: Double = radius

  def getCenter: Point = center

  override def getPath: Path = hex


  def addScratchShapes = {
    val newRadius = CircleCanvas.getRadiusForInnerCircle(radius)
    shapes.foreach(_.remove())
    for(i <- 1 to 10) {
      var p = new Path()
      p.strokeColor = Color(Math.random(), Math.random(), Math.random(), 1)
      p.strokeWidth = 1
      for(j <- 1 to 30) {
        p.add(center.subtract(newRadius / 2).add(new Point(Math.random() * newRadius, Math.random() * newRadius)).multiply(scaleFactor))
      }
      shapes :+= p
      shapesGroup.addChild(p)
    }
  }


  def redrawShapes(scaleFactor: Double) = {

    shapesGroup.removeChildren()
    shapesGroup.remove()
    shapesGroup = Group(js.Array[Item]())
    for (shape <- shapes) {
      shapesGroup.addChild(shape)
      shape.scaleAll(scaleFactor / oldScaleFactor)

  //     TODO: There must be an easier way
        shape.position = new Point(
        shape.position.x / oldScaleFactor * scaleFactor,
        shape.position.y / oldScaleFactor * scaleFactor
      )
      shape.strokeWidth = shape.strokeWidth * scaleFactor / oldScaleFactor
    }
  }
}
