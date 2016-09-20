package nosketch.controls.camera

import org.denigma.threejs.Camera
import org.scalajs.dom.raw.HTMLElement
import vongrid.controls.{DefaultMouseButtons, MouseButtons, OrbitControlsPort}

import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@ScalaJSDefined
class NSOrbitControls(camera: Camera, element: HTMLElement, mouseButtons: MouseButtons = DefaultMouseButtons) extends OrbitControlsPort(camera, element, mouseButtons) {

//
}
