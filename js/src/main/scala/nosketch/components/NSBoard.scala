package nosketch.components

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date

import nosketch.Viewer3D
import vongrid.{Board, Cell, HexGrid, Tile}
import vongrid.config.{AStarFinderConfig, HexGridConfig}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.timers._

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@ScalaJSDefined
class NSBoard(grid: NSGrid, finderConfig: js.UndefOr[AStarFinderConfig] = js.undefined) extends Board(grid, finderConfig) {
  // TODO: Fade in Tiles (crappy implementation!)
//  def generateDroppingTile(cell: VisibleHexagon, duration:Double = 300d, initialHeight: Double = 10): Unit = {
//
//    val timeElapsed = new Date().getTime - cell.created.getTime
//
//    if(timeElapsed < duration) {
//      val newHeight = initialHeight - (timeElapsed / duration * initialHeight)
//      if(newHeight < initialHeight) {
//        cell.h = newHeight
//      }
//    } else {
//      cell.h = 1
//    }
//
//      cell.getTile.map(removeTile)
//      if(!Viewer3D.isOutOfScope(cell.getCenter)) {
//        val newTile = grid.generateTile(cell, 0.97d)
//        // TODO: This is weird! why setting an option via setter
//        cell.setTile(newTile)
//        addTile(newTile)
//      }
//
//
//    if (cell.h > 1) {
//      setTimeout(10) { generateDroppingTile(cell, duration, initialHeight) }
//    }
//  }


  //
//  def this() = this(???, ???)
//  def this(grid: NSGrid) = this(grid, ???)
  //def this(grid: NSGrid, finderConfig: AStarFinderConfig) = this(grid, finderConfig)

  def getGrid = this.grid

}
