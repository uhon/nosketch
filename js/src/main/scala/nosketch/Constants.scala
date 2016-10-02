package nosketch

import org.denigma.threejs.Vector3
import nosketch.controls.camera._

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object HexConstants {
  val angleToHexagonOnSide = Array(0, 60, 120, 180, 240, 300)
  val sideMappings = Array(3, 4, 5, 0, 1, 2) // maps sides of attached hexagon. A hexagon with a neighbour at side x is itself neighbour of the other on sideMappings(x)

}

object GridConstants {
  val tileScaleFactor = 0.95d
  val tileInitialHeight = 2
  val fogColor = 0x000000
}

object CameraConstants {
  val initialCameraPos: Vector3 = new Vector3(0, 50, 50)
}

object SceneConstants {
  val fogColor = 0x000000
}

object OrbitControlsConstants {
  def apply(o: NSOrbitControls) = {
    o.enableKeys = true
    o.enableDamping = true
    o.dampingFactor = 0.1
    o.enableMomentum = true
    o.keyPanSpeed = 10
    o.enableRotate = true
    o.enableZoom = true
    o.zoomSpeed = 2
    o.maxAzimuthAngle = 40 // TODO: find out why!

    //        o.autoRotate = true
    //        o.autoRotateSpeed = 20
    o.minDistance = 30
    o.maxDistance = 350

  }
}