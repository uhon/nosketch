package nosketch.worker

import nosketch.util.facades.Bundle
import org.denigma.threejs._
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g, literal => l}
import scala.scalajs.js.{Dictionary, UndefOr}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("SvgGeometryWorker")
object SvgGeometryWorker {
  val urlQueue = new mutable.Queue[String]

  var loadingSVGs = false

  @JSExport def run(): MessageEvent => Unit = {
    { (event: MessageEvent) =>
      val url = event.data.asInstanceOf[String]

      urlRequest(url)
    }
  }


  def urlRequest(url: String, callback: (js.Tuple2[String, Geometry]) => Unit = g.postMessage(_)) = {
    urlQueue.enqueue(url)
    if (!loadingSVGs) processUrlQueue(callback)
  }


  def processUrlQueue(requesterFunc: (js.Tuple2[String, Geometry]) => Unit): Unit = {
//    g.console.log("process queue in worker")
    val svgLoadStart = System.currentTimeMillis()
    loadingSVGs = true
    var url = ""
    def callbackLoad(err: js.Any, svg: UndefOr[org.scalajs.dom.svg.SVG] = js.undefined): Unit = {
      if (svg.isDefined) {

        val geo = createGeometry(svg.get)
        val geoTuple = js.Tuple2(url, geo)

        requesterFunc(geoTuple)
      } else {
//        println("ERROR " + err)
        requesterFunc(js.Tuple2(url, new Geometry()))
      }
      if(urlQueue.nonEmpty) processUrlQueue(requesterFunc)

      loadingSVGs = false
    }

    if(urlQueue.nonEmpty) {
      url = urlQueue.dequeue()

      Bundle.loadSvg(url, callbackLoad _)
    } else
      loadingSVGs = false
  }

  def createGeometry(svg: SVG) = {

    val svgParseStart = System.currentTimeMillis()
    val svgPath = Bundle.parsePath(svg)

    val mesh3dStart = System.currentTimeMillis()
    // triangulate
    val complex = Bundle.svgMesh3d(
      svgPath,
      l(
        "scale" -> 1,
        "simplify" -> 5,
        "delaunay" -> false
//      "scale" -> 20,
//      "simplify" -> 0,
//      "delaunay" -> false

      )
    )
    Bundle.createGeom(complex)
  }
}
