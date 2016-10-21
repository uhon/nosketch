package nosketch.worker.svg

import org.scalajs.dom.svg
import org.denigma.threejs.{Geometry, Mesh}
import org.scalajs.dom.svg.SVG

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSName}


@js.native
object Bundle extends js.Object {
  def loadSvg(url: String, cb: js.Function2[js.Any, UndefOr[SVG], Unit]) : Unit = js.native
  def parsePath(svg: SVG): String = js.native                 // TODO: js.Object should be real class
  def svgMesh3d(svgPath: String, config: js.Object): Complex = js.native
  def createGeom(complex: Complex): Geometry = js.native
  def jsdom(): JsDom = js.native

}

@js.native
class Complex(value: js.Object, enumerable: Boolean = false, writable: Boolean = true, configurable: Boolean = true) extends Geometry {

}

