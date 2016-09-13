package nosketch.components

import javafx.scene.image.PixelFormat

import nosketch.shared.util.GridConstants
import nosketch.shared.util.GridConstants.tileInitialHeight
import nosketch.util.NSTools
import org.denigma.threejs._
import org.denigma.threejs.THREE
import org.scalajs.dom._

import scalajs.js.timers.setTimeout
import org.scalajs.dom.raw.{Event, HTMLImageElement}
import paperjs.Basic.{Point, Rect, Size}
import paperjs.Items.Raster
import vongrid.Cell

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
class ImageHexagon(grid: NSGrid, q: Double, r: Double, s: Double, h: Double = tileInitialHeight) extends VisibleHexagon(grid, q, r, s, h)  {
  def this(grid: NSGrid) = this(grid, 0, 0, 0, tileInitialHeight)

  draw

  def draw = {

    def onceLoaded(tex: Texture): Unit = {
      getTile.map((t) => {
        //        tex.wrapT = THREE.ClampToEdgeWrapping
        //        tex.wrapS = THREE.ClampToEdgeWrapping
        tex.repeat.set(0.06, 0.06)
        tex.offset.set(0.5, 0.5)

        val color = NSTools.randomizeRGBDouble(10, 30, 90, 13)



        var sideMatParams = l().asInstanceOf[MeshPhongMaterialParameters]
        sideMatParams.color = color
        sideMatParams.shininess = 0xFFFFFF

        val sideMaterial = new MeshPhongMaterial(sideMatParams)

        var faceMatParams = l().asInstanceOf[MeshPhongMaterialParameters]
        faceMatParams.color = 0xFFFFFF
                faceMatParams.emissive = color
                faceMatParams.emissive = color
                faceMatParams.shininess = 0xFFFFFF
                faceMatParams.refractionRatio = 1
                faceMatParams.ambient = color
                faceMatParams.map = tex
        //
        val faceMaterial = new MeshPhongMaterial  (faceMatParams)

        val materials = js.Array[Material]()
        materials.push(faceMaterial)
        (1 to 43).foreach((i) => materials.push(sideMaterial))

        val meshFaceMaterial = new MeshFaceMaterial(materials)
        val oldMesh = t.mesh
        val meshContainer = oldMesh.parent
        console.log("faces:", t.geometry.faces.length, t.geometry.faces)

        t.material = meshFaceMaterial
//        t.mesh = new Mesh(t.geometry, meshFaceMaterial)
//        t.mesh.scale.set(GridConstants.tileScaleFactor, GridConstants.tileScaleFactor, GridConstants.tileScaleFactor)
//        t.mesh.rotation.set(Math.PI/2,0,0)
//        t.mesh.position.set(t.position.x, t.position.y, t.position.z)
        t.mesh.material = meshFaceMaterial
        meshContainer.remove(oldMesh)
        meshContainer.add(t.mesh)
      })
    }


    val tl = new TextureLoader()
    tl.load(nosketch.io.ImageUrls.randomPngShape, (tex: Texture) => onceLoaded(tex))
  }

  override def destroy: Unit = {
    super.destroy
    // implement destroy
  }

}
