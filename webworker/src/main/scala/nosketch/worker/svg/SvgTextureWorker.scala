package nosketch.worker.svg

import nosketch.util.facades.{Bundle, EncodedImageDataTransferable, EncodedImageData}
import nosketch.util.loading.NSTextureLoader
import org.denigma.threejs.Texture
import org.scalajs.dom.raw.HTMLImageElement
import org.scalajs.dom.{MessageEvent, console, html, svg}
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g, literal => l}
import scala.scalajs.js.{JSON, UndefOr}
import scala.scalajs.js.annotation.JSExport

@JSExport object SvgTextureWorker {
  val imageQueue = new mutable.Queue[HTMLImageElement]

  var loadingSVGs = false

  @JSExport def run(): MessageEvent => Unit = {
    { (event: MessageEvent) =>
      val imageDataEncoded = event.data.asInstanceOf[EncodedImageData]
      val imageDataDecoded = Bundle.canvasWebWorker.transfer.decode(imageDataEncoded)

//      console.log("received by webworker", imageDataDecoded)

      imageQueue.enqueue(imageDataDecoded.asInstanceOf[html.Image])

//      println(s"svgWorker received url\n${url}")

      if (!loadingSVGs) processUrlQueue()
    }
  }

  def processUrlQueue(): Unit = {

    if (imageQueue.nonEmpty) {
      loadingSVGs = true
      //console.info("process queue.........")
      val image = imageQueue.dequeue()
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




      val encodedData = NSTextureLoader.getEncodedCanvas(image)
//      console.log("send: ", encodedData)
      g.postMessage(js.Tuple2(Math.random().toString, encodedData.data), js.Array(encodedData.buffer))

      if (imageQueue.nonEmpty) processUrlQueue()
      loadingSVGs = false
    }
  }

}
