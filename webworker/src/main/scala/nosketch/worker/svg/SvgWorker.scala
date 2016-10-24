package nosketch.worker.svg

import org.denigma.threejs.Geometry
import org.scalajs.dom.raw.AbstractWorker
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.svg.SVG
import org.scalajs.dom.webworkers._

import scala.collection.mutable
import scala.scalajs.js.timers._
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport

@JSExport object SvgWorker {
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
    loadingSVGs = true
    var url = ""
    def callbackLoad(err: js.Any, svg: UndefOr[org.scalajs.dom.svg.SVG] = js.undefined): Unit = {
      if (svg.isDefined) {
//        println("SVG loaded!!")

        val svgTuple = js.Tuple2(url, svg.get)

        val geo = createGeometry(svg.get)
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
    val svgPath = Bundle.parsePath(svg)
    // triangulate
    val complex = Bundle.svgMesh3d(
      svgPath,
      l(
        "scale" -> 1000,
        "simplify" -> 0.01
      )
    )
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
