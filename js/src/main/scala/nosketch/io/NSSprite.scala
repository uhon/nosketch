package nosketch.io

import nosketch.components.{NSTile, VisibleHexagon}
import nosketch.hud.DebugHUD
import nosketch.shared.util.FA
import nosketch.util.NSTools
import org.denigma.threejs._
import org.scalajs.dom._
import vongrid.{Board, Tile}
import vongrid.config.AbstractSprite
import vongrid.rendering.BoardSprite
import vongrid.utils.Tools

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.Dynamic.{literal => l}


object ImageUrls {
  val notFound = "/assets/shapes/notFound.png"
  def randomPngShape = {
    s"/assets/font-awesome/white/png/256/${FA(scala.util.Random.nextInt(FA.maxId))}.png"
  }

  // Attention, SVG freezes system wen load as sprites in masses!
  def randomSvgShape = {
    s"/assets/font-awesome/white/svg/${FA(scala.util.Random.nextInt(FA.maxId))}.svg"
  }
}

object Materials {
  def default = new SpriteMaterial(l(
    "color" -> 0xffffff,
    "fog" -> true
  ).asInstanceOf[SpriteMaterialParameters])
}

/**
  * Created by uhon on 27/03/16.
  */
@ScalaJSDefined
/*
	Wraps three.sprite to take care of boilerplate and add data for the board to use.
 */
class NSSprite(
          b: Board,
          var nsTile: NSTile,
          var url: String = ImageUrls.notFound,
          var autoActivate:Boolean = true,
          var mat: SpriteMaterial = Materials.default
) extends BoardSprite {

  var tile: Tile = nsTile
  // settings


  material = mat

  var v = new Vector3(2, 3 ,9)

  scale.set(9,9,9)

  var highlight = new Color(222, 168, 228)

  // how high off the board this object sits
  var heightOffset: Float = 0

  // other objects like the SelectionManager expect these on all objects that are added to the scene
  var active: Boolean = false
  val uniqueId: String = NSTools.generateID()

  var board = b
  var container: Object3D = null
  var geo: BufferGeometry = null
  var texture: Texture = null
  var disposed = false

  visible = false

  val tl = new TextureLoader()
  tl.load(url, (tex:Texture) => {
      if(disposed) {
        tex.dispose()
      } else {
        material.map = tex
        container = nsTile.sprites

        // TODO: extract this
        highlight = new Color(33, 11, 428)
        heightOffset = 2

        // sprite.select
        if (autoActivate) {
          activate(0, 0, 2)
        }
      }
  })

  setTile(tile)


  def setTile(tile: Tile): Unit = {
    board.setEntityOnTile(this, tile)
  }

  def activate(x: Double = 0, y: Double = 0, z: Double = 0) = {
    active = true
    visible = true
    position.add(new Vector3(x, y, z))
    container.add(this)
  }

  def disable = {
    active = false
    visible = false
    container.remove(this)
  }


  def select = material.color = highlight

  def deselect = material.color = new Color(255, 255, 255)

  def dispose = {
    disposed = true
    DebugHUD.spriteDisposes.increment
    container = null
    nsTile.sprites.remove(this)
    tile = null
    nsTile = null
  }
}

