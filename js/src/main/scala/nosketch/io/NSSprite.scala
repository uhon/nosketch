package nosketch.io

import nosketch.Config
import nosketch.components.{NSTile, VisibleHexagon}
import nosketch.hud.DebugHUD
import nosketch.util.io.ImageUrls
import nosketch.util.loading.FA
import nosketch.util.NSTools
import org.denigma.threejs._
import org.scalajs.dom._
import vongrid.{Board, Tile}
import vongrid.config.AbstractSprite
import vongrid.rendering.BoardSprite
import vongrid.utils.Tools

import scala.scalajs.js


import scala.scalajs.js.Dynamic.{literal => l}




object Materials {
  def default = new SpriteMaterial()
}

/**
  * Created by uhon on 27/03/16.
  */
/*
	Wraps three.sprite to take care of boilerplate and add data for the board to use.
 */
class NSSprite(
          b: Board,
          var nsTile: NSTile,
          var preloadedTexture: Texture = null,
          var url: String = ImageUrls.notFound,
          callback: (NSSprite) => Unit = (s: NSSprite) => { s.activate(0, 0, 2) },
          var mat: SpriteMaterial = Materials.default
) extends BoardSprite {

  var tile: Tile = nsTile
  // settings


  material = mat

  var v = new Vector3(2, 3 ,9)

  scale.set(2,2,2)

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

  container = nsTile.sprites
  // TODO: extract this
  highlight = new Color(33, 11, 428)
  heightOffset = 1

  if(preloadedTexture == null) {
    val tl = new TextureLoader()
    tl.load(url, (tex: Texture) => {
      if (disposed) {
        tex.dispose()
      } else {
        material.map = tex
      }
      callback.apply(this)
    })
  } else {
      material.map = preloadedTexture
      callback.apply(this)
  }





  def activate(x: Double = 0, y: Double = 0, z: Double = 0) = {
    active = true
    visible = true
    position.add(new Vector3(x, y, z))
    heightOffset = Config.Sprite.heightOffset
    container.add(this)
    board.setEntityOnTile(this, tile)
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

