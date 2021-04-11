package nosketch.components

import nosketch.Config
import nosketch.util.NSTools
import org.denigma.threejs.{ExtrudeGeometry, Fog, MeshPhongMaterial, THREE, Vector3}
import vongrid.{Cell, HexGrid}
import vongrid.config.{ExtrudeSettings, HexGridConfig, TileConfig}

import scala.scalajs.js
import org.scalajs.dom._
import vongrid.utils.Tools

import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.JSExport
import js.Dynamic.{literal => l}
import scala.scalajs.js.{UndefOr, undefined}

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */

class NSGrid(config: HexGridConfig) extends HexGrid(config) {
  // Is set by Board once it is created
  var board:NSBoard = null

  override val _directions = js.Array[Cell](

    new Cell(+1d, 0d, -1d),
    new Cell(0d, +1d, -1d),
    new Cell(-1d, +1d, 0d),
    new Cell(-1d, 0d, +1d),
    new Cell(0d, -1d, +1d),
    new Cell(+1d, -1d, 0d)
  ).reverse

  def getDirection(i: Int): Cell = _directions(i)


  def this() = this(HexGridConfig.cellSize(11)
        .cameraPosition(Config.Camera.initialCameraPos)
        .fog(new Fog(Config.Grid.fogColor, 200, 400))
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




  def generateNSTile(cell: VisibleHexagon, scale: Double): NSTile = {
    var height = Math.abs(cell.h).toInt
    if (height < 1) height = 1

    var geo = _geoCache(height)
    if (js.isUndefined(geo)) {
      ExtrudeSettings.amount(height)
      geo = new ExtrudeGeometry(this.cellShape, this.extrudeSettings);
      this._geoCache(height) = geo
    }

    /*mat = this._matCache[c.matConfig.mat_cache_id];
    if (!mat) { // MaterialLoader? we currently only support basic stuff though. maybe later
      mat.map = Loader.loadTexture(c.matConfig.imgURL);
      delete c.matConfig.imgURL;
      mat = new THREE[c.matConfig.type](c.matConfig);
      this._matCache[c.matConfig.mat_cache_id] = mat;
    }*/

    val tile = new NSTile(
      board,
      TileConfig
        .size(cellSize)
        .scale(scale)
        .cell(cell)
        .geometry(geo)
        .material(NSTileMaterialFactory.default)
    )


    cell.setTile(tile)

    tile
  }

}
