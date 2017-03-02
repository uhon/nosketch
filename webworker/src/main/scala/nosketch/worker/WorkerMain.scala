package nosketch.worker

import nosketch.util.facades.Bundle
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.webworkers.WorkerGlobalScope

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalajs.js.Dynamic.{global => g}
import org.scalajs.dom.console

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object WorkerMain extends JSApp {
  var delegate: Option[MessageEvent => Unit] = None

  @JSExport
  def main(): Unit = {

    g.window = g.self
//
    g.importScripts("/assets/js/three.js", "/assets/nosketchwebworker-jsdeps.js")

    var jsdom = Bundle.jsdom()
    var jsdomWindow = jsdom.defaultView

    g.document = jsdomWindow.document
    g.Image = Bundle.canvasWebWorker.Image
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
