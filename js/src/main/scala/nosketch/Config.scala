package nosketch

import nosketch.animation.{EaseIn, IdleAnimation, SinusYWobbler}
import nosketch.components.VisibleHexagon

import scala.concurrent.duration._
import org.denigma.threejs.Vector3
import nosketch.controls.camera._

object Config {
  object Hex {
    var geoHeight: Double = 2

    val angleToHexagonOnSide = Array(0, 60, 120, 180, 240, 300)
    val sideMappings = Array(3, 4, 5, 0, 1, 2) // maps sides of attached hexagon. A hexagon with a neighbour at side x is itself neighbour of the other on sideMappings(x)

  }
  object Grid {
    val tileScaleFactor = 0.95d
    val tileInitialHeight = 2
    val fogColor = 0x000000
  }

  object Camera {
    val initialCameraPos: Vector3 = new Vector3(0, 100, 100)
  }

  object Scene {
    val fogColor = 0x000000
  }

  object OrbitControls {
    def apply(o: NSOrbitControls) = {
      o.enableKeys = true
      o.enableDamping = true
      o.dampingFactor = 0.1
      o.enableMomentum = true
      o.keyPanSpeed = 30
      o.enableRotate = true
      o.enableZoom = true
      o.zoomSpeed = 2
      o.maxAzimuthAngle = 40 // TODO: find out why!

      //        o.autoRotate = true
      //        o.autoRotateSpeed = 20
      o.minDistance = 10
      o.maxDistance = Config.World.distanceClipping - Config.World.fogDepth

//      // How far you can zoom in and out ( OrthographicCamera only )
//      minZoom = 0d
//      maxZoom = Config.World.distanceClipping - 60
//      // How far you can orbit vertically, upper and lower limits.
//      // Range is 0 to Math.PI radians.
//      minPolarAngle = 0d
//      // radians
//      maxPolarAngle = Math.PI
//      // radians
//      // How far you can orbit horizontally, upper and lower limits.
//      // If set, must be a sub-interval of the interval [ - Math.PI, Math.PI ].
//      minAzimuthAngle = -Double.MaxValue
//      // radians
//      maxAzimuthAngle = Double.MaxValue

    }
  }

  object AnimationConstants {
    def create(hex: VisibleHexagon) = new SinusYWobbler(hex)
//    def create(hex: VisibleHexagon) = new EaseIn(hex)
//    def create(hex: VisibleHexagon) = IdleAnimation
//
    object EaseIn {
      var height: Double = 27
      var duration: FiniteDuration = 0.7 seconds
    }

    object SinusYWobbler {
      var height: Double = 2
    }
  }

  object Caching {
    var interval: FiniteDuration = 5 milliseconds
    var minNumMeshes: Int = 800
    var minNumRequests: Int = 8
  }

  object World {
    var distanceClipping = 400
    var fogDepth = 30
    var intersectionSphereRadius: Double = 10

  }

  object Sprite {
    var heightOffset = 2
  }

  object Environment {
    var useWorker = true
    var shapeSpecies = ShapeSpecies.GEO
  }
}

object ShapeSpecies extends Enumeration {
  type ShapeSpecies = Value
  val GEO, TEX = Value
}


