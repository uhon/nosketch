package nosketch.util.facades

import org.denigma.threejs.Geometry
import org.scalajs.dom.svg.SVG

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal
object Bundle extends js.Object {
  def loadSvg(url: String, cb: js.Function2[js.Any, UndefOr[SVG], Unit]) : Unit = js.native
  def parsePath(svg: SVG): String = js.native                 // TODO: js.Object should be real class
  def svgMesh3d(svgPath: String, config: js.Object): Complex = js.native
  def createGeom(complex: Complex): Geometry = js.native
  def jsdom(): JsDom = js.native
  def canvasWebWorker: CanvasWebWorker = js.native

}

@js.native
@JSGlobal
class Complex(value: js.Object, enumerable: Boolean = false, writable: Boolean = true, configurable: Boolean = true) extends Geometry {

}

