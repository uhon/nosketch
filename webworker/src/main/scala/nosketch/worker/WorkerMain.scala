package nosketch.worker

import nosketch.worker.svg.Bundle
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.webworkers.WorkerGlobalScope

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalajs.js.Dynamic.{global => g}

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object WorkerMain extends JSApp {
  var delegate: Option[MessageEvent => Unit] = None

  @JSExport
  def main(): Unit = {


    g.window = g.self

    g.importScripts("/assets/js/three.js", "/assets/nosketchwebworker-jsdeps.js")

    var jsdom = Bundle.jsdom()
    println("JSDOM has content: ", jsdom)
    var jsdomWindow = jsdom.defaultView


    g.document = jsdomWindow.document



    println("Starting Worker-Main")
    g.onmessage = (event: MessageEvent) => {
      delegate match {

        case x: Some[MessageEvent => Unit] => x.foreach(_.apply(event))
        case None =>
          delegate = Some(js.eval(event.data.asInstanceOf[String]).asInstanceOf[MessageEvent => Unit])
      }
    }
  }
}
