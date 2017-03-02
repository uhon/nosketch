package nosketch.util.loading

import java.awt.Canvas

import nosketch.util.facades._
import nosketch.util.io.ImageUrls
import nosketch.util.loading.FA._
import org.denigma.threejs._

import scala.annotation.tailrec
import scala.scalajs.js.timers._
import org.scalajs.dom._
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.HTMLImageElement

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, _}

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object NSTextureLoader {


  val lm = new LoadingManager()
  val tl = new TextureLoader(lm)
  var textureCache = mutable.Map[String, Texture]()

  def loadFA(callback: (Map[FA, Texture]) => Unit) = {


    var fontAwesome = FA.values.zipWithIndex.toList

    def incLoad(i: Int, acc: Map[FA, Texture], reportBack: (Map[FA, Texture]) => Unit): Unit = {

      // FIXME: only subset is loaded
      if(i < 20) {
        val url = ImageUrls.pngShape(fontAwesome(i)._1.toString)
        load(
          url,
          (tex: Texture) => {
            val newAcc = acc.+(fontAwesome(i)._1 -> tex)
            incLoad(i+1, newAcc, reportBack)
          }
        )
      } else {
        reportBack(acc)
      }
    }

    incLoad(0, Map(), callback)
//    val map = Map[FA, Texture]()
//    callback(map)
  }

  private def loadWithTextureLoader(url: String, callback: (Texture) => Unit):  Unit = {

    val lm = new LoadingManager()
    val tl = new TextureLoader(lm)
//    console.log("cache does not contain url")

    lm.onError = () => {
      load(ImageUrls.notFound, callback)
    }
    //      DebugHUD.texturesLoaded.increment
    tl.load(url, (t: Texture) => {
//      console.log("textureCache", t)
      textureCache += url -> t
      callback(textureCache(url))
    })

  }

  private def loadWithCanvas2D(url: String, callback: (Texture) => Unit): Unit = {


    getDataUrl(url, (dataUri:String) => {
      loadFromDataUri(dataUri, (t: Texture) => {
        textureCache += url -> t
        callback(textureCache(url))
      })

    })
  }

  def loadFromDataUri(dataUri: String, callback: (Texture) => Unit): Unit = {

    // TODO: This wont be necessary at all:
    val lm = new LoadingManager()
    lm.onError = () => {
      console.log("error happened during loading of encoded data")
//      load(ImageUrls.notFound, callback)
    }

    /////

    val tl = new TextureLoader(lm)
    tl.load(dataUri, (t: Texture) => {
//      console.log("textureCache", t)
      callback(t)
    })
  }

  def loadFromEncodedData(encodedData: EncodedCanvas, callback: (Texture) => Unit): Unit = {
    // TODO: This wont be necessary at all:
    val lm = new LoadingManager()
    lm.onError = () => {
      console.error("Error happened while trying to load from encoded")
    }

    /////

    val tl = new TextureLoader(lm)

    console.log("drawingTime main-thread")
    val decodedCanvas = Bundle.canvasWebWorker.transfer.decode(encodedData)

    //    val decodedImageData = decodedCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D].getImageData(0, 0, 1792, 1792)
    //
    //    console.log("DECODED DATA", decodedImageData)
    //    val t = new DataTexture(decodedImageData.data.asInstanceOf[ImageData])
    //    t.needsUpdate = true
    //    val t = new DataTexture(decodedImageData, 1792, 1792, THREE.RGBFormat, THREE.ByteType, THREE., THREE.ClampToEdgeWrapping, THREE.ClampToEdgeWrapping, THREE.NearestFilter, THREE.NearestFilter)

    //console.info(decodedCanvas.toDataURL("image/png"))

    //    callback(new DataTexture(decodedCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D].getImageData(0,0,512,512)))
    tl.load(decodedCanvas.toDataURL("image/png"), (t: Texture) => callback(t))
    console.log("end drawing on main-thread")
    //
//    tl.load(decodedImageData, (t: Texture) => {
//      console.log("textureCache", t)
//
//    })
  }






  def load(url: String, callback: (Texture) => Unit): Unit = {
//    console.log("loading " + url)
    if(textureCache.contains(url)) {

      //      DebugHUD.texturesCached.increment
      callback(textureCache(url))

    } else {

      loadWithTextureLoader(url, callback)
//      loadWithCanvas2D(url, callback)
    }

  }

  def getEncodedImage(image: html.Image): EncodedImageTransferable = {
    Bundle.canvasWebWorker.transfer.encode(image)
  }

  def getEncodedCanvas(image: html.Image): EncodedCanvasTransferable = {
    val workerCanvas = js.Dynamic.newInstance(Bundle.canvasWebWorker.Canvas)(512, 512).asInstanceOf[html.Canvas]
    workerCanvas.width = 512
    workerCanvas.height = 512

    val context = workerCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    context.fillStyle = "#00ff00"
    context.strokeStyle = "#ff0000"
//    context.fillRect(0, 0, workerCanvas.width, workerCanvas.height)
    context.drawImage(image, 0, 0, 512, 512, 512, 512)

    val encodedCanvas = Bundle.canvasWebWorker.transfer.encode(workerCanvas)

    encodedCanvas
  }


  /**
    * Main-Thread only (does not work with canvas-webworker (npm plugin)
    */
  def getDataUrl(url: String, callback: (String) => Unit) = {
    var image = document.createElement("img").asInstanceOf[HTMLImageElement]

    image.onload = (event: Event) => {
//      console.log("loaded image")
      val canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
      canvas.width = 512
      canvas.height = 512

      val context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      context.drawImage(image, 0, 0)
        callback(canvas.toDataURL("image/png"))
    }

    image.src = url
  }

}
