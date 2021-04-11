package nosketch

import nosketch.animation.{EaseIn, IdleAnimation, SinusYWobbler}
import nosketch.components.VisibleHexagon

import scala.concurrent.duration._
import org.denigma.threejs.Vector3
import nosketch.controls.camera._
import vongrid.Cell

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
    val funkyText = Seq(
      new Cell(0, 2, -2),
      new Cell( 1, 1, -2),
      new Cell( 1, 0, -1),
      new Cell( 0, 0, 0),
      new Cell( -1, 2, -1),
      new Cell( 3, -2, -1),
      new Cell( 4, -4, 0),
      new Cell( 5, -4, -1),
      new Cell( -4, 4, 0),
      new Cell( -3, 4, -1),
      new Cell( -2, 4, -2),
      new Cell( -6, 6, 0),
      new Cell( -5, 6, -1),
      new Cell( -4, 6, -2),
      new Cell( -7, 6, 1),
      new Cell( -7, 8, -1),
      new Cell( -6, 8, -2),
      new Cell( -8, 8, 0),
      new Cell( -7, 9, -2),
      new Cell( -8, 10, -2),
      new Cell( -9, 10, -1),
      new Cell( -10, 10, 0),
      new Cell( -5, 5, 0),
      new Cell( -8, 6, 2),
      new Cell( -9, 8, 1),
      new Cell( -11, 10, 1),
      new Cell( 8, -6, -2),
      new Cell( 2, 6, -8),
      new Cell( -1, 5, -4),
      new Cell( -5, 10, -5),
      new Cell( 6, 1, -7),
      new Cell( -2, 2, 0),
      new Cell( -1, 0, 1),
      new Cell( -2, 1, 1),
      new Cell( 2, -2, 0),
      new Cell( 1, -2, 1),
      new Cell( 2, -3, 1),
      new Cell( 3, -4, 1),
      new Cell( 6, -4, -2),
      new Cell( 4, -2, -2),
      new Cell( 22, -18, -4),
      new Cell( -2, 7, -5),
      new Cell( -1, 7, -6),
      new Cell( 0, 7, -7),
      new Cell( 1, 7, -8),
      new Cell( -2, 6, -4),
      new Cell( 0, 6, -6),
      new Cell( 0, 5, -5),
      new Cell( 2, 5, -7),
      new Cell( 2, 3, -5),
      new Cell( 3, 3, -6),
      new Cell( 4, 3, -7),
      new Cell( 5, 3, -8),
      new Cell( 6, 2, -8),
      new Cell( 2, 2, -4),
      new Cell( 3, 1, -4),
      new Cell( 5, -1, -4),
      new Cell( 6, -1, -5),
      new Cell( 7, -1, -6),
      new Cell( 8, -1, -7),
      new Cell( 9, -1, -8),
      new Cell( 8, -2, -6),
      new Cell( 9, -3, -6),
      new Cell( 10, -3, -7),
      new Cell( 11, -3, -8),
      new Cell( -4, 10, -6),
      new Cell( -3, 10, -7),
      new Cell( -2, 10, -8),
      new Cell( -6, 11, -5),
      new Cell( -5, 9, -4),
      new Cell( -6, 10, -4)
    )

  }

  object Camera {
    val initialCameraPos: Vector3 = new Vector3(26.93727178563309, 468.1783929249074, 95.01345055746958)
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
      o.zoomSpeed = 1
      o.maxAzimuthAngle = Double.MaxValue // TODO: find out why!

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
      var height: Double = 1.5
    }
  }

  object Caching {
    var interval: FiniteDuration = 5 milliseconds
    var minNumMeshes: Int = 800
    var minNumRequests: Int = 8
  }

  object World {
    var distanceClipping = 600
    var fogDepth = 50
    var intersectionSphereRadius: Double = 20

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


