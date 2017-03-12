package nosketch.components

import nosketch.Viewer3D
import nosketch.hud.DebugHUD
import nosketch.io.NSSprite
import nosketch.util.loading.FA
import nosketch.util.NSTools
import org.denigma.threejs._
import paperjs.Paths.Path
import vongrid.Tile
import vongrid.config.TileConfig
import vongrid.utils.Tools

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.timers._
import scala.concurrent.duration._
import js.Dynamic.{global => g}
import js.Dynamic.{literal => l}
import org.scalajs.dom._

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
@ScalaJSDefined
class NSTile(board: NSBoard, config: TileConfig) extends Tile(config) {
  DebugHUD.tileCreations.increment
  val sprites: Object3D = new Object3D

  var controlsHidden = true

  def getCell: Option[VisibleHexagon] = {
    cell match {
      case v:VisibleHexagon => Option(v)
      case _ => Option.empty // If its null, or not a VisibleHexagon, we just give an empty Option
    }
  }

  def showControls = {

//    setTimeout(100 milliseconds) {
      getCell.map((c) => {
        controlsHidden = false

        val tex = Viewer3D.predefinedTextures.get(FA.asterisk)
  //      console.log("tex:", tex.get.toString)
        if(tex.isDefined) {
          new NSSprite(board, this, tex.get, "", (sprite: NSSprite) => {
            if (!controlsHidden) {
              DebugHUD.spriteShowCtrl.increment
              DebugHUD.activeCell.setValue(new Vector3(c.q, c.r, c.s))
              sprite.scale.set(5,5,5)
              sprite.activate(10, 2, 10)
            } else {
              sprite.dispose
            }
          })
        }
      })
//    }
  }

  def hideControls = {
    DebugHUD.spriteHideCtrl.increment
    disposeSprites
    controlsHidden = true
  }

  def setCell(cell: VisibleHexagon) = {
    this.cell = cell
  }

  override def dispose(): js.Any = {
    DebugHUD.tileDisposes.increment
    board.group.remove(sprites)
    disposeSprites
    super.dispose()
  }

  def disposeSprites: Unit = {
    sprites.children.foreach((s) => {
      s match {
        case s:NSSprite => s.dispose
        case _ => console.log("Invalid sprite detected! Something weird happened, there should be no other Object3D but NSSprites on this tile")
      }
    })

  }
  
  override def toggle() = {
    mesh.material match {
      case m:MeshFaceMaterial => {
        m.materials(0) match {
          case face: MeshPhongMaterial =>
            face.color = new Color(0, 0xff, 0)
            face.needsUpdate = true
        }
      }
      case _ =>
    }
    super.toggle()
  }
}

object NSTileMaterialFactory {
  def default = {
    new MeshPhongMaterial(defaultMeshPhongParams)
  }
}

object defaultMeshPhongParams extends MeshPhongMaterialParameters {
  color = NSTools.randomizeRGBDouble(10, 30, 90, 0)
}



