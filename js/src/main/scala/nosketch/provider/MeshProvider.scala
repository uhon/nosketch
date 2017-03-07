package nosketch.provider

import javax.swing.plaf.nimbus.ImageScalingHelper

import nosketch.{Config, Viewer3D}
import nosketch.components.ImageHexagon
import nosketch.hud.DebugHUD
import nosketch.hud.elements.debug.TextIndicator
import nosketch.util.io.ImageUrls
import org.denigma.threejs.{Geometry, Mesh, MeshPhongMaterial, MeshPhongMaterialParameters}
import org.scalajs.dom._
import org.scalajs.dom.raw.Worker
import org.scalajs.dom.svg.SVG

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scala.concurrent.duration._
import scala.scalajs.js.timers._

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@JSExport("mp")
object MeshProvider {
  @JSExport
  val shapeRequests = new mutable.Queue[(ImageHexagon, (Mesh) => Unit)]

  var numberOfAdds = 0

  @JSExport
  val meshes = new mutable.Queue[(String, Mesh)]

  @JSExport
  val workerRequests: ListBuffer[String] = new ListBuffer()

  private var servingEnabled = false

  DebugHUD.addElement(new TextIndicator("# shape requests", () => shapeRequests.size))
  DebugHUD.addElement(new TextIndicator("# worker requests", () => workerRequests.size))
  DebugHUD.addElement(new TextIndicator("# queued meshes ", () => meshes.size))

  var  svgGeometryWorkers: js.Array[Worker] = js.Array()

  def init = {
    (0 to 4).foreach((i) => {
      val worker = new Worker("/assets/nosketchwebworker-fastopt.js")
      instrumentWorker(worker)
      svgGeometryWorkers.push(worker)

    })
  }

  def isServingEnabled = servingEnabled

  def enableServing = if(!servingEnabled) { servingEnabled = true; serveRequesters }

  def disableServing = servingEnabled = true

  def instrumentWorker(worker: Worker) = {
    worker.onmessage = { (reply: js.Any) =>
      reply match {
        case r: MessageEvent => {
          r.data match {
            case a: Any => {
              val geoTuple = a.asInstanceOf[js.Tuple2[String, Geometry]]
              // TODO: Not sure if it is the right place to create the mesh, since it puts pressure onto main-thread
              if(geoTuple._2.vertices.length > 0) {
                val newMesh = createMesh(geoTuple._2)
                //Viewer3D.board.group.add(newMesh)

                val newTuple = (geoTuple._1, newMesh)
                meshes.enqueue(newTuple)
              }
              workerRequests -= geoTuple._1
            }

          }
        }
        case x: Any => {
          console.log(x.asInstanceOf[js.Object])
        }
      }
    }
//    worker.postMessage("nosketch.worker.svg.SvgGeometryWorker().run()")
    worker.postMessage("nosketch.worker.SvgGeometryWorker().run()")
  }



  // Initially we want to have a full queue!
  def addOneToSvgQueue(url: String): Unit = {
    workerRequests += url
    // Round Robin would make more sense (probably)
    val currentWorker = numberOfAdds % svgGeometryWorkers.length
    svgGeometryWorkers(currentWorker).postMessage(url)
    numberOfAdds += 1
  }

  def createMesh(geo: Geometry) = {
    val start = System.currentTimeMillis()

    val newGeometry = new Geometry()
    newGeometry.vertices = geo.vertices
    newGeometry.faces = geo.faces

    //    val material = new ShaderMaterial(materialSettings)
    val material = new MeshPhongMaterial(meshPhongMaterialSettings)

    //      console.log("creating mesh at", tile.position.x, tile.position.y + 2, tile.position.z)
    val mesh = new Mesh(newGeometry, material)
    //      console.log("creating newMesh at", tile.position.x, tile.position.y + 2, tile.position.z)
    mesh.rotation.y = Math.PI
    //    newMesh.rotation.z = Math.PI / 2
    mesh.rotation.x = -Math.PI / 2
    mesh.scale.set(5, 5, 5)

    DebugHUD.meshCreation.setValue(System.currentTimeMillis() - start)

    mesh

  }

  def gimmeShape(imageHexagon: ImageHexagon, callback: (Mesh) => Unit) = {
//    setTimeout(Math.random() * 2) {
      if (meshes.nonEmpty) {

        callback(meshes.dequeue()._2)
      } else {
        shapeRequests enqueue ((imageHexagon, callback))
      }
//    }
  }

  def serveRequesters: Unit = {
    if(servingEnabled)

      if(shapeRequests.nonEmpty && meshes.nonEmpty) {
        val request = shapeRequests.dequeue()
//        setTimeout(1 milliseconds) {
//            serveRequesters
//        }
        if (request._1.disposed) {
          // This request is outdated, take the next
          serveRequesters
        } else {
          request._2.apply(meshes.dequeue()._2)
        }
//        serveRequesters
      } else {
//        setTimeout(10 milliseconds) {
//          serveRequesters
//        }
      }

    }


  def startCaching(): Unit = {
    if(Config.Caching.minNumMeshes > meshes.size )
      if(Config.Caching.minNumRequests > workerRequests.size)
        0.to(Config.Caching.minNumRequests  - workerRequests.size).foreach(_ => addOneToSvgQueue(ImageUrls.randomSvgShape))

    setTimeout(Config.Caching.interval) {
      startCaching()
    }
  }

}


object meshPhongMaterialSettings extends MeshPhongMaterialParameters {
  color = 0xFFFFFF
}
