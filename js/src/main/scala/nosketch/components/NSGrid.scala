package nosketch.components

import org.denigma.threejs.{Fog, Vector3}
import vongrid.{Cell, HexGrid}
import vongrid.config.HexGridConfig

import scala.scalajs.js
import org.scalajs.dom._
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.{JSExport, ScalaJSDefined}
import js.Dynamic.{literal => l}
/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */

@ScalaJSDefined
class NSGrid(config: HexGridConfig) extends HexGrid(config) {

  val _directions = js.Array[Cell](

    new Cell(+1d, 0d, -1d),
    new Cell(0d, +1d, -1d),
    new Cell(-1d, +1d, 0d),
    new Cell(-1d, 0d, +1d),
    new Cell(0d, -1d, +1d),
    new Cell(+1d, -1d, 0d)
  ).reverse

  def getDirection(i: Int): Cell = _directions(i)


  def this() = this(HexGridConfig.cellSize(11)
        .cameraPosition(new Vector3(0, 0, 150))
        .fog(new Fog(0xFFFFFF, 200, 400))
  )

  def getVisibleCells = cells.asInstanceOf[js.Dictionary[VisibleHexagon]]

  /*  ________________________________________________________________________
  Hexagon-specific conversion math
  Mostly commented out because they're inlined whenever possible to increase performance.
  They're still here for reference.
 */

  override def _createVertex(i: Int): Vector3 = {
    val angle = vongrid.TAU / 6 * i
    new Vector3(this.cellSize * Math.cos(angle), this.cellSize * Math.sin(angle), 0)
  }
}
