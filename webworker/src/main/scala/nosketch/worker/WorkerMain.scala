package nosketch.worker

import nosketch.util.facades.Bundle
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.webworkers.WorkerGlobalScope

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalajs.js.Dynamic.{global => g}
import org.scalajs.dom.console

import scala.scalajs.js.isUndefined

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@JSExportTopLevel("WorkerMain")
object WorkerMain {
  var delegate: Option[MessageEvent => Unit] = None

  @JSExport
  def main(args: Array[String]): Unit = {

//    g.window = g.self
//
    g.importScripts("/assets/js/three.js", "/assets/js/bundle.js", "/assets/nosketchwebworker-jsdeps.js")


    if (!isUndefined(Bundle.canvasWebWorker.Image)) {
      g.Image = Bundle.canvasWebWorker.Image
    }
//    g.Canvas = Bundle.canvasWebWorker.Canvas



    println("Starting Worker-Main")
    g.onmessage = (event: MessageEvent) => {

      delegate match {

        case x: Some[MessageEvent => Unit] => x.foreach(_.apply(event))
        case None =>
          val tmp = event.data.asInstanceOf[String]
          console.log("delegate:", tmp)
//          if(!tmp.endsWith(".svg")) {
            delegate = Some(js.eval(tmp).asInstanceOf[MessageEvent => Unit])
//          }
      }
    }
  }
}
