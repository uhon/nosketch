package nosketch.components

import javafx.scene.image.PixelFormat

import nosketch.animation.{EaseIn, IdleAnimation, SinusYWobbler}
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.TextIndicator

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import nosketch.provider.{MeshProvider, ShapeTextureProvider}
import nosketch.util.io.ImageUrls
import nosketch.{Config, ShapeSpecies, Viewer3D}
import nosketch.util.loading.NSTextureLoader
import nosketch.util.NSTools
import nosketch.worker.SvgGeometryWorker
import org.denigma.threejs.THREE
import org.scalajs.dom._

import scalajs.js.timers.setTimeout
import org.scalajs.dom.raw.{Event, HTMLImageElement}
import paperjs.Basic.{Point, Rect, Size}
import paperjs.Items.Raster
import vongrid.Cell
import org.denigma.threejs.Color

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, _}
import scala.scalajs.js.timers._
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.timers._
import js.Dynamic.{global => g}
import js.Dynamic.{literal => l}
import org.denigma.threejs.{Texture, _}
import org.denigma.threejs.extras.HtmlSprite
import org.denigma.threejs.extensions.Container3D
import org.scalajs.dom.svg.SVG

import scala.concurrent.Future
import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.{Any, Dictionary, Function2, UndefOr}


/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@ScalaJSDefined
class ImageHexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = Config.Grid.tileInitialHeight)
extends VisibleHexagon(grid, q, r, s, h)
{
  var animationEnabled = false
  var animationMethod: nosketch.animation.Animation = nosketch.animation.IdleAnimation

  def this(grid: NSGrid) = this(grid, 0, 0, 0, Config.Grid.tileInitialHeight)
  val group = new Object3D
  Viewer3D.board.group.add(group)

  var shape: Option[Mesh] = None

  def renderSvg(svg: SVG, delay: Double = 0d): Unit = {
    console.log("render SVG")
  }




  def applyTexture(tex: Texture): Unit = {
//    console.log("tex", tex)
    getTile.foreach((t) => {
      tex.wrapT = THREE.ClampToEdgeWrapping
      tex.wrapS = THREE.ClampToEdgeWrapping
      tex.repeat.set(0.06, 0.06)
      tex.offset.set(0.5, 0.5)
      tex.anisotropy = 0
      tex.premultiplyAlpha =  false
      tex.magFilter = THREE.NearestFilter
      tex.minFilter = THREE.NearestFilter

      colorize(Some(tex))

    })


  }

  // TODO: Deserves a better Name and misses Comments
  def colorize(tex: Option[Texture] = None): Unit = {

    getTile.foreach((t) => {
      val color = NSTools.randomizeRGBDouble(30, 50, 120, 53)

      //      console.log("color", color)

      val sideMatParams = l().asInstanceOf[MeshPhongMaterialParameters]
//      sideMatParams.color = color
      // Make it a Rainbow
      sideMatParams.color = Math.random() * 0xffffff
      sideMatParams.shininess = 0xFFFFFF

      val sideMaterial = new MeshPhongMaterial(sideMatParams)

      val fmp = l().asInstanceOf[MeshPhongMaterialParameters]
      fmp.color = color // color drawing

      if(tex.isDefined) {
        //      fmP.emissive = 0x336699
        //      fmP.ambient = 0x000000
        fmp.refractionRatio = 1000
        fmp.shininess = 100
        fmp.transparent = false
        fmp.emissive = 0xff0000
        tex.foreach(fmp.emissiveMap = _)
        fmp.emissiveIntensity = 1d
      }

      val fm = new MeshPhongMaterial(fmp)


      val materials = js.Array[Material]()
      materials.push(fm)
      (1 to t.mesh.geometry.faces.length).foreach((_) => materials.push(sideMaterial))

      val meshFaceMaterial = new MeshFaceMaterial(materials)


      t.mesh.material = meshFaceMaterial
      //        t.mesh = new Mesh(t.geometry, meshFaceMaterial)
      //        t.mesh.scale.set(GridConstants.tileScaleFactor, GridConstants.tileScaleFactor, GridConstants.tileScaleFactor)
      //        t.mesh.rotation.set(Math.PI/2,0,0)
      //        t.mesh.position.set(t.position.x, t.position.y, t.position.z)

      //        meshContainer.remove(oldMesh)
      //        meshContainer.add(t.mesh)

      //
      //      val svgProducer = Viewer3D.system.actorOf(Props(new SvgProducer()))
      //      val receiveActor = Viewer3D.system.actorOf(svgReceiveActor)
      //
      //      Viewer3D.system.scheduler.scheduleOnce(0 second)(
      //        receiveActor.!((ImageUrls.randomSvgShape, svgProducer))
      //      )

      /*      implicit val timeout = Timeout(5 seconds)
            val future = svgProducer. ImageUrls.randomSvgShape
            val result = Await.result(future, timeout.duration).asInstanceOf[String]
            println(result)*/
      //
      //      implicit val timeout = Timeout(5 seconds)
      //      val future2: Future[SVG] = ask(svgProducer, ImageUrls.randomSvgShape).mapTo[SVG]
      //      val result2 = Await.result(future2, 100 second)
      //      console.log("result2", result2.textContent)
    })
  }



  def draw: NSTile = {
    import ShapeSpecies._
    Config.Environment.shapeSpecies match {
      case GEO => requestShapeAsMesh()
      case TEX => requestShapeAsTexture()
    }

    getTile.get
  }

  private def requestShapeAsTexture() = {
    if(Config.Environment.useWorker) {
      ShapeTextureProvider.gimmeShape(this, (tex: Texture) => {
        if (!disposed) {
          grid.generateNSTile(this, Config.Grid.tileScaleFactor)
          group.add(getTile.get.sprites)
          Viewer3D.board.addTile(tile)
          applyTexture(tex)
          Viewer3D.requestViewUpdate
        }
      })
    } else {
      NSTextureLoader.load(ImageUrls.randomPngShape, (tex: Texture) => {
        if (!disposed) {
          grid.generateNSTile(this, Config.Grid.tileScaleFactor)
          group.add(getTile.get.sprites)
          Viewer3D.board.addTile(tile)
          applyTexture(tex)
        }
      })
    }
  }

  private def requestShapeAsMesh() = {
    if (!disposed) {
      grid.generateNSTile(this, Config.Grid.tileScaleFactor)
      group.add(getTile.get.sprites)
      tile.material.transparent = true
      tile.material.opacity = 0
      Viewer3D.board.addTile(tile)

      MeshProvider.gimmeShape(this, producer)
    }
  }

  private def producer(mesh: Mesh) = {
    if (! disposed) {

      //val newMesh = ImageHexagon.createNewMesh(mesh)
      shape = Some(mesh)
      mesh.position.set(tile.position.x, tile.position.y + Config.Hex.geoHeight, tile.position.z)

      colorize()

      group.add(mesh)

      animationMethod = Config.AnimationConstants.create(this)
      animationEnabled = true
    }
  }

  override def destroy: Unit = {
    group.children.foreach(group.remove)
    Viewer3D.board.group.remove(group)
    super.destroy
    // TODO: implement destroy (everything clean?)
  }
  override def animate(): Unit = {
    if(animationEnabled) {
      DebugHUD.animationCycles.increment
      animationEnabled = animationMethod.animationLoop
//      Viewer3D.requestSceneUpdate
      // FIXME: request scene update should be enough!
      // Probably not, since assign-neighbours finds hexagons by pixel coordinates, hex might not be there
      // if this happens, so maybe a new one is created. do research on extra hexagons crated (which might overlap another)?
      Viewer3D.requestViewUpdate

    }
  }
}
