package nosketch.components

import javafx.scene.image.PixelFormat

import nosketch.{GridConstants, Viewer3D}
import nosketch.loading.NSTextureLoader
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


/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
@ScalaJSDefined
class ImageHexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = GridConstants.tileInitialHeight) extends VisibleHexagon(grid, q, r, s, h)  {
  def this(grid: NSGrid) = this(grid, 0, 0, 0, GridConstants.tileInitialHeight)


  def applyTexture(tex: Texture): Unit = {
    getTile.map((t) => {
      tex.wrapT = THREE.ClampToEdgeWrapping
      tex.wrapS = THREE.ClampToEdgeWrapping
      tex.repeat.set(0.06, 0.06)
      tex.offset.set(0.5, 0.5)
      tex.anisotropy = 0
      tex.premultiplyAlpha =  false
      tex.magFilter = THREE.NearestFilter
      tex.minFilter = THREE.NearestFilter

      val color = NSTools.randomizeRGBDouble(30, 50, 120, 53)

      console.log("color", color)

      val sideMatParams = l().asInstanceOf[MeshPhongMaterialParameters]
      sideMatParams.color = Math.random() * 0xffffff
//      sideMatParams.shininess = 0xFFFFFF

      val sideMaterial = new MeshPhongMaterial(sideMatParams)

      val fmp = l().asInstanceOf[MeshPhongMaterialParameters]
      fmp.color = color // color drawing
//      fmP.emissive = 0x336699
//      fmP.ambient = 0x000000
      fmp.refractionRatio = 1000
      fmp.shininess = 100
      fmp.transparent = false
//      fmP.transparent = true
//      fmP.alphaTest = 0xFFFFFF
//      fmP.refractionRatio = 1
//      fmP.alphaMap = tex
//      fmP.opacity = 1
      NSTextureLoader.load("/assets/images/white.png", (tt) => {
        tt.repeat.set(0.06, 0.06)
        tt.offset.set(0.5, 0.5)
        fmp.alphaMap = tt

      fmp.map = tex
//      NSTextureLoader.load("/assets/shapes/notFound.png", (t) => {
//        fmP.alphaMap = t})
      //
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
      // TODO: Check if needed
       Viewer3D.requestViewUpdate
    })
    })
  }

  def draw = {
    NSTextureLoader.load(nosketch.io.ImageUrls.randomPngShape, (tex: Texture) => {
      if (!disposed) {
        grid.generateNSTile(this, GridConstants.tileScaleFactor)
        Viewer3D.board.group.add(getTile.get.sprites)
        Viewer3D.board.addTile(tile)
        applyTexture(tex)
      }
    })
  }

  override def destroy: Unit = {
    super.destroy
    // implement destroy
  }

}
