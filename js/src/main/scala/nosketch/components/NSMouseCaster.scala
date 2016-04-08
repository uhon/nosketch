package nosketch.components

import org.denigma.threejs.{Camera, Object3D}
import org.scalajs.dom.raw.{Event, HTMLElement, MouseEvent}
import org.scalajs.dom.console

import scalajs.js.{UndefOr, undefined}
import vongrid.utils.{MC, MouseCaster}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@ScalaJSDefined
class NSMouseCaster(group: Object3D, camera: Camera, element: UndefOr[HTMLElement] = js.undefined) extends MouseCaster(group, camera, element) {

//  override def _onDocumentMouseMove(evt: MouseEvent): js.Any = {
//
//
//    if(down) {
//      console.log("mouse drages")
//      signal.dispatch(MC.DRAG,  this.pickedObject)
//    }
//
//    super._onDocumentMouseDown(evt)
//  }

}
