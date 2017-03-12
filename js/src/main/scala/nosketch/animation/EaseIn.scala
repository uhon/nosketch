package nosketch.animation

import nosketch.components.{ImageHexagon, NSTile, VisibleHexagon}
import nosketch.Config
import org.scalajs.dom._
import org.denigma.threejs.{Color, Mesh, MeshFaceMaterial, MeshPhongMaterial, Vector3}

import scala.concurrent.duration.FiniteDuration


class EaseIn(hex: VisibleHexagon) extends Animation {
  val tile: NSTile = hex.getTile.get
  val originalY: Double = tile.position.y
  val height: Double = Config.AnimationConstants.EaseIn.height
  val duration: FiniteDuration = Config.AnimationConstants.EaseIn.duration
  var startTime: Long = System.currentTimeMillis()
  var started = false

  override def animationLoop: Boolean = {
    if (!started) {
      started = true
      startTime = System.currentTimeMillis() -1
      tile.position.setY(height)
    }

    val timeDelta: Double = System.currentTimeMillis() - startTime

    if (timeDelta < duration.toMillis) {
      val percentage = timeDelta / duration.toMillis

      val cosDelta = Math.cos(percentage * Math.PI/2)

      tile.mesh.material match {
        case m: MeshFaceMaterial => {
          m.materials(0) match {
            case face: MeshPhongMaterial =>
              face transparent = true
              face.opacity = 1 - cosDelta
//              face.color = new Color(0xff, 0, 0)
//              face.needsUpdate
          }
        }
      }
      hex match {
        case i:ImageHexagon => i.shape.foreach((s) => {
          s.material.transparent = true
          s.material.opacity = 1 - cosDelta
        })
      }

      val newY = originalY + cosDelta * height
//      val newY = originalY + height - percentage * height
      tile.position.setY(newY)

      updateChildrensHeight(newY)

    } else {
      tile.position.setY(originalY)
      updateChildrensHeight(originalY)
      started = false
    }
    started
  }

  def updateChildrensHeight(height: Double) = {
    hex match {
      case x: ImageHexagon => x.group.children.foreach(c => c.position.setY(height + Config.Hex.geoHeight))
    }
  }
}