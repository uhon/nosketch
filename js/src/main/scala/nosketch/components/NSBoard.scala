package nosketch.components

import vongrid.{Board, HexGrid}
import vongrid.config.{AStarFinderConfig, HexGridConfig}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@ScalaJSDefined
class NSBoard(grid: NSGrid, finderConfig: js.UndefOr[AStarFinderConfig] = js.undefined) extends Board(grid, finderConfig) {

//
//  def this() = this(???, ???)
//  def this(grid: NSGrid) = this(grid, ???)
  //def this(grid: NSGrid, finderConfig: AStarFinderConfig) = this(grid, finderConfig)

  def getGrid = this.grid
}
