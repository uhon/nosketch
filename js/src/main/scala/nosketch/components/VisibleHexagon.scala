package nosketch.components

import java.time.LocalDate
import java.util.Date

import nosketch.Config.Hex
import nosketch.animation.IdleAnimation
import nosketch.Viewer3D
import org.scalajs.dom.console
import nosketch.hud.DebugHUD
import org.denigma.threejs.{Object3D, Vector3}
import paperjs.Basic.Point
import paperjs.Paths.Path
import paperjs.Styling.Color
import paperjs.Items.{Item, Layer}
import vongrid.{AbstractCell, Cell, Tile}

import scala.scalajs.js.timers._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.util.Random

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
@ScalaJSDefined
abstract class VisibleHexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = 0d)
  extends Cell(q, r, s, h)
{
  def animate(): Unit = {
    // We do nothing here (can be overwritten by children
  }

  DebugHUD.cellCreations.increment
  val created = new Date()

  var disposed = false

  var neighbours: js.Array[_ >: Cell] = js.Array[Cell](PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon, PhantomHexagon)

  var hud = new Object3D

  def getTile: Option[NSTile] = {
    tile match {
      case t:NSTile => Option(t)
      case null => Option.empty // If its null, or not a NSTile, we just give an empty Option
      case t:js.Any => console.log("weird, there should only be NSTiles present", t); Option.empty // If its null, or not a NSTile, we just give an empty Option
      case _ => console.log("weird, something else caught"); Option.empty // If its null, or not a NSTile, we just give an empty Option
    }
  }

  def getGrid = grid

  def setTile(tile: NSTile) = this.tile = tile

  def getCenter = grid.cellToPixel(this)

  def destroy: Any = {
    DebugHUD.cellDisposes.increment
    // TODO: REMOVE DATA
    getTile.map(_.dispose())

    for (i <- 0 to 5) {
      neighbours(i) match {
        case x: VisibleHexagon => x.neighbours(Hex.sideMappings(i)) = PhantomHexagon
        case PhantomHexagon =>
      }
    }
    disposed = true
  }




  /**
   * Assign new hexagons as neighbour if they fit into the bounds of the current view
   * newly on screen
 *
   * @return
   */
  def assignNeighbours: Any = {
//    val order = Stream.continually(Random.nextInt(5)).distinct.take(5)
    val order = 0 to 5
    val placesToGrow: ListBuffer[VisibleHexagon] = ListBuffer()
    for(i <- order  ) {
      //console.log("=== checking neighbour " + i)
      neighbours(i) match {
        case x: VisibleHexagon =>
        case PhantomHexagon => {
          val neighbourCenter = PhantomHexagon.calculateCellAtCenter(this, grid.getDirection(i))
//          console.log("Neighbour is a Phantom  with center", neighbourCenter)
          //console.log("creating new Hexagon at Pos", center.toString())
          // TODO: calc position of new hex by adding directions-cell
          val hexTuple = Viewer3D.findOrCreateHexagon(grid.cellToPixel(neighbourCenter))

          neighbours(i) = hexTuple._1
          hexTuple match {
            case (v: VisibleHexagon, justCreated:Boolean) =>
              if(justCreated) {
                v.neighbours(Hex.sideMappings(i)) = this
                placesToGrow += v
//                distributeNeighbourKnowledge(this, v, i)
//                v.assignNeighbours
              }
            case _ =>
          }
        }
      }
    }

//    util.Random.shuffle(placesToGrow)foreach(_.assignNeighbours)
    placesToGrow.foreach(_.assignNeighbours)
  }

  def distributeNeighbourKnowledge(origHex: VisibleHexagon, newHex: VisibleHexagon, origHexSide: Int) = {


    val origLeftNeighbour = origHex.neighbours(sideLeftOf(origHexSide))
    val origRightNeighbour = origHex.neighbours(sideRightOf(origHexSide))

    val newHexSide = Hex.sideMappings(origHexSide)

    val newHexSideLeft = sideLeftOf(newHexSide)
    val newHexSideRight = sideRightOf(newHexSide)

    val leftNeihbourSide = sideLeftOf(Hex.sideMappings(sideLeftOf(origHexSide)))
    val rightNeihbourSide = sideRightOf(Hex.sideMappings(sideRightOf(origHexSide)))

//    console.log(s"""side: $origHexSide, newSide: $newHexSide, newHexLeft: $newHexSideLeft, newHexRight: $newHexSideRight, leftNeighbourSide: $leftNeihbourSide, rightNeighbourSide: $rightNeihbourSide""")


    origLeftNeighbour match {
      case v: VisibleHexagon => v.neighbours(leftNeihbourSide) = newHex; newHex.neighbours(newHexSideRight) = v
      case _ =>
    }

    origRightNeighbour match {
      case v: VisibleHexagon => v.neighbours(rightNeihbourSide) = newHex; newHex.neighbours(newHexSideLeft) = v
      case _ =>
    }
  }

  def sideLeftOf(side: Int) = if(side +1 > 5) 0 else side +1
  def sideRightOf(side: Int) = if(side -1 < 0) 5 else side -1
}
