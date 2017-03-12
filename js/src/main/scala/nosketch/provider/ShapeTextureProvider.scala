package nosketch.provider

import nosketch.components.ImageHexagon
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.TextIndicator
import nosketch.util.facades._
import nosketch.util.io.ImageUrls
import nosketch.util.loading.NSTextureLoader
import org.denigma.threejs._
import org.scalajs.dom._
import org.scalajs.dom.html.Image
import org.scalajs.dom.raw.{HTMLImageElement, Worker}
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.{JSON, JavaScriptException, UndefOr}
import scala.scalajs.js.annotation.JSExport

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@JSExport("stp")
object ShapeTextureProvider {
  @JSExport
  val shapeRequests = new mutable.Queue[(ImageHexagon, (Texture) => Unit)]

  @JSExport
  val textures = new mutable.Queue[(String, Texture)]

  DebugHUD.addElement(new TextIndicator("# tex-shape requests", () => shapeRequests.size))
  DebugHUD.addElement(new TextIndicator("# queued textures ", () => textures.size))

  var  svgTextureWorkers: js.Array[Worker] = js.Array()

  def init() = {
    (0 to 2).foreach((i) => {
      val worker = new Worker("/assets/nosketchwebworker-fastopt.js")
      instrumentWorker(worker)
      svgTextureWorkers.push(worker)
    })
  }

  def instrumentWorker(worker: Worker) = {
    worker.onmessage = { (reply: js.Any) =>
      reply match {
        case r: MessageEvent => {
          //console.info("received response from worker")
          val textureTuple = r.data.asInstanceOf[js.Tuple2[String, EncodedCanvas]]
          //console.info("received: ", textureTuple._1, textureTuple._2)

          NSTextureLoader.loadFromEncodedData(textureTuple._2, (t) => {
            textures.enqueue((textureTuple._1, t))
          })
        }
      }
    }
    worker.postMessage("nosketch.worker.svg.SvgTextureWorker().run()")
  }



  // Initially we want to have a full queue!
  def addOneToSvgQueue(url: String): Unit = {
    //console.info(s"adding encodedData to Workerqueue", encodedData)

    svgTextureWorkers((Math.random() * svgTextureWorkers.length).floor.toInt).postMessage(url)
  }






  def gimmeShape(imageHexagon: ImageHexagon, callback: (Texture) => Unit) = {
    shapeRequests.enqueue((imageHexagon, callback))
  }

  def serveRequesters: Unit = {
    while(shapeRequests.nonEmpty && textures.nonEmpty) {
      val request = shapeRequests.dequeue()
      if(request._1.disposed) {
        // This request is outdated, take the next
        serveRequesters
      } else {
        val texture = textures.dequeue()._2
        //texture.needsUpdate
        request._2.apply(texture)
      }
    }
  }


  def fillQueueWithRandomSVGs(amount: Int): Unit = {
    (0 to amount).foreach { i =>
      addOneToSvgQueue(ImageUrls.randomSvgShape)
    }
  }

}
