package nosketch.worker.svg

import org.scalajs.dom.MessageEvent

import scala.scalajs.js.timers._
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport

@JSExport object SvgWorker {
  @JSExport def run(): MessageEvent => Unit = {
    { (event: MessageEvent) =>
      val url = event.data.asInstanceOf[String]

      println(s"loading and creating geometry: $url")


      def callbackLoad(err: js.Any, svg: UndefOr[org.scalajs.dom.svg.SVG] = js.undefined): Unit = {
        if (svg.isDefined) {
          println("loaded SVG, process it", svg.get)

          // grab all <path> data
          val svgPath = Bundle.parsePath(svg.get)
          // triangulate
          val complex = Bundle.svgMesh3d(
            svgPath,
            l(
              "scale" -> 1000,
              "simplify" -> 0.01
            )
          )
          // play with this value for different aesthetic
          // randomization: 500,

          //    // split mesh into separate triangles so no vertices are shared
          //    complex = reindex(unindex(complex.positions, complex.cells))
          //
          //    // we will animate the triangles in the vertex shader
          //    const attributes = getAnimationAttributes(complex.positions, complex.cells)
          //
          //    // build a ThreeJS geometry from the mesh primitive
          //    console.log("rendering")
          val geometry = Bundle.createGeom(complex)


          scalajs.js.Dynamic.global.postMessage(geometry)
        }
      }

      Bundle.loadSvg(url, callbackLoad _)
//      scalajs.js.Dynamic.global.postMessage("Hello")



    }
  }
}
