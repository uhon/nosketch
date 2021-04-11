package nosketch.worker.svg

import nosketch.util.facades.{Bundle, EncodedImageData, EncodedImageDataTransferable}
import nosketch.util.loading.NSTextureLoader
import org.denigma.threejs.Texture
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLImageElement
import org.scalajs.dom._
import org.scalajs.dom.svg.{Image, SVG}

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g, literal => l}
import scala.scalajs.js.{JSON, UndefOr}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("SvgTextureWorker") object SvgTextureWorker {
  val urlQueue = new mutable.Queue[String]

  var loadingSVGs = false

  @JSExport def run(): MessageEvent => Unit = {
    { (event: MessageEvent) =>

//      console.log("received by webworker", imageDataDecoded)

      val url = event.data.asInstanceOf[String]

      urlQueue.enqueue(url)

//      println(s"svgWorker received url\n${url}")

      if (!loadingSVGs) processUrlQueue()
    }
  }

  def processUrlQueue(): Unit = {

    if (urlQueue.nonEmpty) {
      loadingSVGs = true
      //console.info("process queue.........")
      val url = urlQueue.dequeue()
//      console.info("require this: ", js.eval("Image"))

//      var workerCanvas = js.eval(s"""
//                 var canvasWebWorker = require("canvas-webworker");
//                 var Canvas = canvasWebWorker.Canvas;
//                 var Image = canvasWebWorker.Image;
//
//                 new Canvas(720, 480);
//              """).asInstanceOf[html.Canvas]



//      val workerImage = js.Dynamic.newInstance(Bundle.canvasWebWorker.Image)().asInstanceOf[HTMLImageElement]
//      workerImage.width = 1792
//      workerImage.height = 1792


//      val image = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
      val image = js.Dynamic.newInstance(Bundle.canvasWebWorker.Image)().asInstanceOf[HTMLImageElement]

      image.onload = (e: Event) => {
        console.log("send: ", image)
        val encodedData = NSTextureLoader.getEncodedCanvas(image)
        console.log("send: ", encodedData)
        g.postMessage(js.Tuple2(Math.random().toString, encodedData.data), js.Array(encodedData.buffer))
      }

      image.src = url


      if (urlQueue.nonEmpty) processUrlQueue()
      loadingSVGs = false
    }
  }

}
