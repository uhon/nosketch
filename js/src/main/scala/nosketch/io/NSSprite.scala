package nosketch.io

import nosketch.components.VisibleHexagon
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
  def randomSVGShape = s"/assets/shapes/${ (Math.random() * 15).round }.svg"
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
          var tile: Tile,
          var url: String = ImageUrls.notFound,
          var mat: SpriteMaterial = Materials.default
) extends BoardSprite {

  // settings


  material = mat

  var v = new Vector3(2, 3 ,9)

  scale.set(9,9,9)

  var highlight = new Color(222, 168, 228)

  // how high off the board this object sits
  var heightOffset: Float = 0

  // other objects like the SelectionManager expect these on all objects that are added to the scene
  var active: Boolean = false
  val uniqueId: String = Tools.generateID()

  var board = b
  var container: Object3D = null
  var geo: BufferGeometry = null
  var texture: Texture = null

  visible = false

  val tl = new TextureLoader()
  tl.load(url, (tex:Texture) => {
        material.map = tex
        container = board.group

        // TODO: extract this
        highlight = new Color(33, 11, 428)
        heightOffset = 2

        // sprite.select
        activate(4,7,2)
  })


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
    container = null
    tile.buttons.remove(this)
    tile = null
  }
}

