package nosketch.provider

import nosketch.components.ImageHexagon
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.TextIndicator
import nosketch.io.ImageUrls
import org.denigma.threejs.Geometry
import org.scalajs.dom._
import org.scalajs.dom.raw.Worker
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@JSExport("sp")
object ShapeProvider {
  @JSExport
  val shapeRequests = new mutable.Queue[(ImageHexagon, (Geometry) => Unit)]

  @JSExport
  val geometries = new mutable.Queue[(String, Geometry)]

  DebugHUD.addElement(new TextIndicator("# shape requests", () => ShapeProvider.shapeRequests.size))
  DebugHUD.addElement(new TextIndicator("# queued gemoetries ", () => ShapeProvider.geometries.size))

  var  svgWorkers: js.Array[Worker] = js.Array()

  def init = {
    (0 to 4).foreach((i) => {
      val worker = new Worker("/assets/nosketchwebworker-fastopt.js")
      instrumentWorker(worker)
      svgWorkers.push(worker)

    })
  }

  def instrumentWorker(worker: Worker) = {
    worker.onmessage = { (reply: js.Any) =>
      reply match {
        case r: MessageEvent => {
          val geoTuple = r.data.asInstanceOf[js.Tuple2[String, Geometry]]
          //          println(s"Received Geometry, applying it")
          //          console.info("loaded geometry vertices", geoTuple._2.vertices)

          val newGeo = new Geometry()
          newGeo.vertices = geoTuple._2.vertices
          newGeo.faces = geoTuple._2.faces
          geometries.enqueue((geoTuple._1, newGeo))
        }
      }
    }
    worker.postMessage("nosketch.worker.svg.SvgWorker().run()")
  }



  // Initially we want to have a full queue!
  def addOneToSvgQueue(url: String): Unit = {
//    console.info(s"adding $url to queue")
    svgWorkers((Math.random() * svgWorkers.length).floor.toInt).postMessage(url)
  }






  def gimmeShape(imageHexagon: ImageHexagon, callback: (Geometry) => Unit) = {
    shapeRequests.enqueue((imageHexagon, callback))
  }

  def serveRequesters: Unit = {
    while(shapeRequests.nonEmpty && geometries.nonEmpty) {
      val request = shapeRequests.dequeue()
      if(request._1.disposed) {
        // This request is outdated, take the next
        serveRequesters
      } else {
        request._2.apply(geometries.dequeue()._2)
      }
    }
  }


  def fillQueueWithRandomSVGs(amount: Int): Unit = {
    0.to(amount).foreach(_ => addOneToSvgQueue(ImageUrls.randomSvgShape))
  }

}
