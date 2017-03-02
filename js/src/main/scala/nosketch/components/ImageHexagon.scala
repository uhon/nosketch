package nosketch.components

import javafx.scene.image.PixelFormat

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import nosketch.provider.{ShapeGeometryProvider, ShapeTextureProvider}
import nosketch.util.io.ImageUrls
import nosketch.{GridConstants, Viewer3D}
import nosketch.util.loading.NSTextureLoader
import nosketch.util.NSTools
import org.denigma.threejs._
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
import scala.scalajs.js.{Any, Function2, UndefOr}


/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@ScalaJSDefined
class ImageHexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = GridConstants.tileInitialHeight) extends VisibleHexagon(grid, q, r, s, h) {
  def this(grid: NSGrid) = this(grid, 0, 0, 0, GridConstants.tileInitialHeight)

  val group = new Object3D
  Viewer3D.board.group.add(group)

  def renderSvg(svg: SVG, delay: Double = 0d) = {
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
  def colorize(tex: Option[Texture] = None) = {

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
      (1 to t.mesh.geometry.faces.length).foreach((i) => materials.push(sideMaterial))

      val meshFaceMaterial = new MeshFaceMaterial(materials)


      t.mesh.material = meshFaceMaterial
      //        t.mesh = new Mesh(t.geometry, meshFaceMaterial)
      //        t.mesh.scale.set(GridConstants.tileScaleFactor, GridConstants.tileScaleFactor, GridConstants.tileScaleFactor)
      //        t.mesh.rotation.set(Math.PI/2,0,0)
      //        t.mesh.position.set(t.position.x, t.position.y, t.position.z)
      t.mesh.material = meshFaceMaterial

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

  def draw = {

// Uncomment this to load textures from png images
//    NSTextureLoader.load(ImageUrls.randomPngShape, (tex: Texture) => {
//      if (!disposed) {
//        grid.generateNSTile(this, GridConstants.tileScaleFactor)
//        group.add(getTile.get.sprites)
//        Viewer3D.board.addTile(tile)
//        applyTexture(tex)
//      }
//    })

// Uncomment this to load Textures via Webworkers
//    ShapeTextureProvider.gimmeShape(this, (tex: Texture) => {
//        if (!disposed) {
//          grid.generateNSTile(this, GridConstants.tileScaleFactor)
//          group.add(getTile.get.sprites)
//          Viewer3D.board.addTile(tile)
//          applyTexture(tex)
//          Viewer3D.requestViewUpdate
//        }
//      })
//

// Uncomment this to load geometrys from svg-data via Webworkers
    if (!disposed) {
      grid.generateNSTile(this, GridConstants.tileScaleFactor)
      group.add(getTile.get.sprites)
      Viewer3D.board.addTile(tile)
      ShapeGeometryProvider.gimmeShape(this, producer)
    }





    def producer(geometry: Geometry) = {
      // our shader material
      val material = new ShaderMaterial(materialSettings)

      val mesh = new Mesh(geometry, material)

      mesh.position.set(tile.position.x, tile.position.y + 2, tile.position.z)
//      console.log("creating mesh at", tile.position.x, tile.position.y + 2, tile.position.z)
      mesh.rotation.y = Math.PI
      //    mesh.rotation.z = Math.PI / 2
      mesh.rotation.x = -Math.PI / 2
      mesh.scale.set(5, 5, 5)
      //    mesh.rotation.x = - Math.PI
      //    console.log("mesh", mesh)
      colorize()
      group.add(mesh)
//      console.log("added mesh")
      Viewer3D.requestViewUpdate
    }
  }

  override def destroy: Unit = {
    group.children.foreach(group.remove(_))
    Viewer3D.board.group.remove(group)
    super.destroy
    // TODO: implement destroy (everything clean?)
  }

}

object materialSettings extends ShaderMaterialParameters {
  val color = 0xffffff
  
  side = THREE.DoubleSide
  //      vertexShader: vertShader,
  //      fragmentShader: fragShader,
  transparent = true
  //      attributes: attributes,
  uniforms = l(
    "opacity" -> l("type" -> "f", "value" -> 1 ),
    "scale" -> l("type" -> "f", "value" -> 1 ),
    "animate" -> l("type" -> "f", "value" -> 1 )
  )

  val defaultAttributeValues = l(
    "color" -> js.Array(1, 1, 1),
    "uv" -> js.Array(0, 0),
    "uv2" -> js.Array(0, 0)
  )
}