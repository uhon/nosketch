package nosketch.provider

import nosketch.components.ImageHexagon
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.TextIndicator
import nosketch.util.io.ImageUrls
import org.denigma.threejs.Geometry
import org.scalajs.dom._
import org.scalajs.dom.raw.Worker
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scala.concurrent.duration._
import scala.scalajs.js.timers._

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@JSExport("sp")
object ShapeGeometryProvider {
  @JSExport
  val shapeRequests = new mutable.Queue[(ImageHexagon, (Geometry) => Unit)]

  @JSExport
  val geometries = new mutable.Queue[(String, Geometry)]

  DebugHUD.addElement(new TextIndicator("# shape requests", () => shapeRequests.size))
  DebugHUD.addElement(new TextIndicator("# queued gemoetries ", () => geometries.size))

  var  svgGeometryWorkers: js.Array[Worker] = js.Array()

  def init = {
    (0 to 4).foreach((i) => {
      val worker = new Worker("/assets/nosketchwebworker-fastopt.js")
      instrumentWorker(worker)
      svgGeometryWorkers.push(worker)

    })
  }

  def instrumentWorker(worker: Worker) = {
    worker.onmessage = { (reply: js.Any) =>
      reply match {
        case r: MessageEvent => {
          // if just a string is returned, log it to console
//          if(r.data.isInstanceOf[String]) {

//          }
          val geoTuple = r.data.asInstanceOf[js.Tuple2[String, Geometry]]
          //          println(s"Received Geometry, applying it")
//                    console.info("loaded geometry vertices", JSON.stringify(geoTuple._2.vertices))

          val newGeo = new Geometry()
          newGeo.vertices = geoTuple._2.vertices
          newGeo.faces = geoTuple._2.faces
          geometries.enqueue((geoTuple._1, newGeo))
        }
        case x: Any => {
          console.log(x)
        }
      }
    }
//    worker.postMessage("nosketch.worker.svg.SvgGeometryWorker().run()")
    worker.postMessage("nosketch.worker.SvgGeometryWorker().run()")
  }



  // Initially we want to have a full queue!
  def addOneToSvgQueue(url: String): Unit = {
//    console.info(s"adding $url to queue")
    // Round Robin would make more sense (probably)
    svgGeometryWorkers((Math.random() * svgGeometryWorkers.length).floor.toInt).postMessage(url)
  }






  def gimmeShape(imageHexagon: ImageHexagon, callback: (Geometry) => Unit) = {
    shapeRequests.enqueue((imageHexagon, callback))
  }

  def serveRequesters: Unit = {
    if(shapeRequests.nonEmpty && geometries.nonEmpty) {
      val request = shapeRequests.dequeue()
      if(request._1.disposed) {
        // This request is outdated, take the next
//        serveRequesters
      } else {
        request._2.apply(geometries.dequeue()._2)
      }
      // Without setTimeout all requesters get served at once which blocks main thread and all sketches appear
      // simultanously (after a period).
      // setTimeout is a workaround to take pressure from main-thread.
      // TODO: Idea. It could be taken view-refreshes into account (maybe via global counter). After a view update the next
      // request could be served
      setTimeout(6 milliseconds) {
        serveRequesters
      }
    }
  }


  def fillQueueWithRandomSVGs(amount: Int): Unit = {
    0.to(amount).foreach(_ => addOneToSvgQueue(ImageUrls.randomSvgShape))
  }

}
