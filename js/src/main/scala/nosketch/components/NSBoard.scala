package nosketch.components

import vongrid.{Board, Cell, HexGrid}
import vongrid.config.{AStarFinderConfig, HexGridConfig}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.timers._

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

  def fadeIn(cell: Cell) {
    setTimeout(5d) {
      if(cell.h > 1) {
        cell.h = cell.h - 10d
        removeTile(cell.tile)
        addTile(grid.generateTile(cell, 0.97d))
        fadeIn(cell)
      }
    }
  }
}
