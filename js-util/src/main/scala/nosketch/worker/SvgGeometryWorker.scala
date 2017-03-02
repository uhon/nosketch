package nosketch.worker

import nosketch.util.facades.Bundle
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g, literal => l}
import scala.scalajs.js.UndefOr
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
    val svgLoadStart = System.nanoTime
    loadingSVGs = true
    var url = ""
    def callbackLoad(err: js.Any, svg: UndefOr[org.scalajs.dom.svg.SVG] = js.undefined): Unit = {
      if (svg.isDefined) {

        //        println("svg load took:" + (svgLoadStart - System.nanoTime()))

        val svgTuple = js.Tuple2(url, svg.get)

        val geo = createGeometry(svg.get)

//        scalajs.js.Dynamic.global.postMessage(js.Array("SVG loaded!!"))
        //        println("created Geometry", geo)
        scalajs.js.Dynamic.global.postMessage(js.Tuple2(url, geo))
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
