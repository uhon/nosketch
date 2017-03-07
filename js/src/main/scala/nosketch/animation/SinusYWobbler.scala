package nosketch.animation

import nosketch.Config
import nosketch.components.{ImageHexagon, NSTile, VisibleHexagon}
import org.denigma.threejs.Vector3
import nosketch.controls.camera._

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js.annotation.ScalaJSDefined


class SinusYWobbler(hex: VisibleHexagon) extends Animation {
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

    val newY = Math.sin(timeDelta * 0.007) * Config.AnimationConstants.SinusYWobbler.height
    tile.position.setY(newY)
    hex match {
      case x: ImageHexagon => x.group.children.foreach(c => c.position.setY(newY + Config.Hex.geoHeight))
    }
    started
  }
}