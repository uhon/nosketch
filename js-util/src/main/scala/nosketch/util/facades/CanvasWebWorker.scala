package nosketch.util.facades

import org.scalajs.dom.{ImageData, Transferable}
import org.scalajs.dom.html.{Canvas, Image}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, JSGlobal}
import scala.scalajs.js.typedarray.Uint8ClampedArray

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@js.native
@JSGlobal("canvasWebWorker")
trait CanvasWebWorker extends js.Object {
  def Canvas: js.Dynamic = js.native
  def Image: js.Dynamic = js.native
  def transfer: Transfer = js.native
}

@js.native
trait Transfer extends js.Any {
  def decode(encodedImageData: EncodedImageData): ImageData = js.native
  def decode(encodedImage: EncodedImage): Image = js.native
  def decode(encodedCanvas: EncodedCanvas): Canvas = js.native
  def encode(canvas: Canvas): EncodedCanvasTransferable = js.native
  def encode(image: Image): EncodedImageTransferable = js.native
  def encode(imageData: ImageData): EncodedImageDataTransferable = js.native
}

@js.native
trait EncodedImageDataTransferable extends js.Any {
  val buffer: Transferable = js.native
  val data: EncodedImageData = js.native
}

@js.native
trait EncodedCanvasTransferable extends js.Any {
  val buffer: Transferable = js.native
  val data: EncodedCanvas = js.native
}

@js.native
trait EncodedImageTransferable extends js.Any {
  val buffer: Transferable = js.native
  val data: EncodedImage = js.native
}

@js.native
trait EncodedImageData extends js.Any {
  @JSName("type")
  val `type`: String = js.native
  val imageData: ImageData = js.native
}

@js.native
trait EncodedImage extends js.Any {
  @JSName("type")
  val `type`: String = js.native
  val imageData: ImageData = js.native
}

@js.native
trait EncodedCanvas extends js.Any {
  @JSName("type")
  val `type`: String = js.native
  val canvas: ImageData = js.native
}

//@js.native
//@JSName("Image")
//class Image extends js.Object {
//}
//
//
//@js.native
//@JSName("Canvas")
//class Canvas extends js.Object{
////  this(width: Int, height: Int) = this()
//}
