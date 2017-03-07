package nosketch.worker

import nosketch.util.facades.Bundle
import org.denigma.threejs.{Mesh, ShaderMaterial, ShaderMaterialParameters, THREE}
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g, literal => l}
import scala.scalajs.js.{Dictionary, UndefOr}
import scala.scalajs.js.annotation.JSExport

@JSExport object SvgGeometryWorker {
  val urlQueue = new mutable.Queue[String]

  var loadingSVGs = false

  @JSExport def run(): MessageEvent => Unit = {
    { (event: MessageEvent) =>
      val url = event.data.asInstanceOf[String]

      urlQueue.enqueue(url)

//      println(s"svgWorker received url\n${url}")

      if (!loadingSVGs) processUrlQueue()
    }
  }

  def processUrlQueue(): Unit = {
    g.console.log("process queue in worker")
    val svgLoadStart = System.nanoTime
    loadingSVGs = true
    var url = ""
    def callbackLoad(err: js.Any, svg: UndefOr[org.scalajs.dom.svg.SVG] = js.undefined): Unit = {
      if (svg.isDefined) {

        //        println("svg load took:" + (svgLoadStart - System.nanoTime()))

        val svgTuple = js.Tuple2(url, svg.get)

        val geo = createGeometry(svg.get)

        // our shader material
        val material = new ShaderMaterial(materialSettings)

        val mesh = new Mesh(geo, material)
//
//        //      console.log("creating mesh at", tile.position.x, tile.position.y + 2, tile.position.z)
//        mesh.rotation.y = Math.PI
//        //    mesh.rotation.z = Math.PI / 2
//        mesh.rotation.x = -Math.PI / 2
        mesh.scale.set(5, 5, 5)
        //    mesh.rotation.x = - Math.PI
        //    console.log("mesh", mesh)

        mesh.userData = url
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("castShadow")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("children")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("drawMode")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("eulerOrder")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("frustumCulled")
////        mesh.asInstanceOf[Dictionary[js.Object]].delete("id")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("layers")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("matrixAutoUpdate")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("matrixWorldNeedsUpdate")
////        mesh.asInstanceOf[Dictionary[js.Object]].delete("modelViewMatrix")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("name")
////        mesh.asInstanceOf[Dictionary[js.Object]].delete("normalMatrix")

////        mesh.asInstanceOf[Dictionary[js.Object]].delete("position")
////        mesh.asInstanceOf[Dictionary[js.Object]].delete("quaternion")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("receiveShadow")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("renderOrder")
////        mesh.asInstanceOf[Dictionary[js.Object]].delete("rotation")
////        mesh.asInstanceOf[Dictionary[js.Object]].delete("scale")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("type")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("up")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("useQuaternion")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("userData")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("uuid")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("visible")
//        mesh.asInstanceOf[Dictionary[js.Object]].delete("__proto__")



        mesh.material.asInstanceOf[Dictionary[js.Object]].delete("blendSrcAlpha")
        mesh.material.asInstanceOf[Dictionary[js.Object]].delete("blendDstAlpha")
        mesh.material.asInstanceOf[Dictionary[js.Object]].delete("blendEquationAlpha")
        mesh.material.asInstanceOf[Dictionary[js.Object]].delete("clippingPlanes")
        mesh.material.asInstanceOf[Dictionary[js.Object]].delete("precision")
        mesh.material.asInstanceOf[Dictionary[js.Object]].delete("index0AttributeName")
        mesh.matrix.asInstanceOf[Dictionary[js.Object]].delete("elements")
        mesh.matrixWorld.asInstanceOf[Dictionary[js.Object]].delete("elements")
        mesh.asInstanceOf[Dictionary[js.Object]].delete("parent")
        mesh.rotation.asInstanceOf[Dictionary[js.Object]].delete("onChangeCallback")
        mesh.quaternion.asInstanceOf[Dictionary[js.Object]].delete("onChangeCallback")
        mesh.geometry.asInstanceOf[Dictionary[js.Object]].delete("boundingBox")
        mesh.geometry.asInstanceOf[Dictionary[js.Object]].delete("boundingSphere")
//        g.deleteFunction(mesh.rotation, "onChangeCallback")



//        g.console.log("mesh", mesh)
        g.console.log("can be cloned", g.canBeCloned(mesh))

//        g.console.log("try to post message")

//        scalajs.js.Dynamic.global.postMessage(js.Array("SVG loaded!!"))
        //        println("created Geometry", geo)
        scalajs.js.Dynamic.global.postMessage(mesh)
//        g.console.log("message posted!!!!")
      } else {
        println("ERROR " + err)
      }
      if(urlQueue.nonEmpty) processUrlQueue()
      loadingSVGs = false
    }

    if(urlQueue.nonEmpty) {
      url = urlQueue.dequeue()

      Bundle.loadSvg(url, callbackLoad _)
    } else
      loadingSVGs = false
  }

  def createGeometry(svg: SVG) = {
//    println("do the math......")
    // grab all <path> data

    val svgParseStart = System.nanoTime()
    val svgPath = Bundle.parsePath(svg)
    println("svgParse took: " + (svgParseStart - System.nanoTime()))

    val mesh3dStart = System.nanoTime()
    // triangulate
    val complex = Bundle.svgMesh3d(
      svgPath,
      l(
        "scale" -> 1,
        "simplify" -> 50,
        "delaunay" -> false

      )
    )
    println("mesh3d took: " + (mesh3dStart - System.nanoTime()))

    // play with this value for different aesthetic
    // randomization: 500,

    //    // split mesh into separate triangles so no vertices are shared
    //    complex = reindex(unindex(complex.positions, complex.cells))
    //
    //    // we will animate the triangles in the vertex shader
    //    const attributes = getAnimationAttributes(complex.positions, complex.cells)
    //
    //    // build a ThreeJS geometry from the mesh primitive
    //    console.log("rendering")
    Bundle.createGeom(complex)
  }
}

object materialSettings extends ShaderMaterialParameters {


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
  //
  //  val defaultAttributeValues = l(
  //    "color" -> js.Array(1, 1, 1),
  //    "uv" -> js.Array(0, 0),
  //    "uv2" -> js.Array(0, 0)
  //  )
}
