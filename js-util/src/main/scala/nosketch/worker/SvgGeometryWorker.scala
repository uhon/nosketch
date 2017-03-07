package nosketch.worker

import nosketch.util.facades.Bundle
import org.denigma.threejs._
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
//    g.console.log("process queue in worker")
    val svgLoadStart = System.currentTimeMillis()
    loadingSVGs = true
    var url = ""
    def callbackLoad(err: js.Any, svg: UndefOr[org.scalajs.dom.svg.SVG] = js.undefined): Unit = {
      if (svg.isDefined) {

        //        println("svg load took:" + (svgLoadStart - System.currentTimeMillis()))

        val geo = createGeometry(svg.get)
        val geoTuple = js.Tuple2(url, geo)

        g.postMessage(geoTuple)
//        g.console.log("message posted!!!!")
      } else {
        println("ERROR " + err)
        g.postMessage(js.Tuple2(url, new Geometry()))
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

    val svgParseStart = System.currentTimeMillis()
    val svgPath = Bundle.parsePath(svg)
//    println("svgParse took: " + (svgParseStart - System.currentTimeMillis()))

    val mesh3dStart = System.currentTimeMillis()
    // triangulate
    val complex = Bundle.svgMesh3d(
      svgPath,
      l(
        "scale" -> 1,
        "simplify" -> 5,
        "delaunay" -> false

      )
    )
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
}
