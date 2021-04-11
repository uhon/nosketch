package nosketch.util.facades

import org.scalajs.dom.raw.Window

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, JSGlobal}

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@js.native
trait JsDom extends js.Object {
  // TODO: This might be incorrect (its not a full blown browser window), ok for just accessing document
  var defaultView: Window = js.native
}
