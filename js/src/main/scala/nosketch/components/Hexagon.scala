package nosketch.components


import paperjs.Paths.Path



/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
class Hexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = 0d, var showInnerCircle: Boolean)
  extends VisibleHexagon(grid, q, r, s, h)
{


  var shapes = List[Path]()


  def addShape(theNewShape: Path): Any = {
    shapes :+= theNewShape
  }

}
