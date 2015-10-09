package nosketch.components

import paperjs.Basic.Point

class Cluster(center: Point, scaleFactor: Double) {

  var hexagons: List[Hexagon] = List()

  val hexagonRadius = 50

  hexagons ::= new Hexagon(center, hexagonRadius, scaleFactor)

  val innerRadius = CircleCanvas.getRadiusForInnerCircle(hexagonRadius)

  val vectorRing1 = new Point(0).add(new Point(innerRadius, 0))
  for(i <- 0 to 5) {
    vectorRing1.angle = 30 + i * 60

    val hexCenter = center.add(vectorRing1)
    hexagons ::= new Hexagon(hexCenter, hexagonRadius, scaleFactor)
  }


  for(i <-  1 to 12) {
    val distanceInnerRing = 2 * Math.sqrt(Math.pow(innerRadius, 2) - Math.pow(innerRadius / 2, 2))
    val vectorRing2Inner = new Point(0).add(new Point(distanceInnerRing, 0))
    val vectorRing2Outer = new Point(0).add(new Point(2 * innerRadius, 0))
    val offsetVector = if(i % 2 == 0) vectorRing2Outer else vectorRing2Inner

    offsetVector.angle = 30 + i * 30

    val hexCenter = center.add(offsetVector)
    hexagons ::= new Hexagon(hexCenter, hexagonRadius, scaleFactor)
  }

  // add some testing lines to the hexagons
  hexagons.foreach(_.addScratchShapes)


  def redraw(scaleFactor: Double) = {
    hexagons.foreach(_.redraw(scaleFactor))
  }
}