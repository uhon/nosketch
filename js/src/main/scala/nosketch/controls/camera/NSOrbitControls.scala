package nosketch.controls.camera

import nosketch.Config
import nosketch.hud.DebugHUD
import org.denigma.threejs.{Camera, Matrix4, OrthographicCamera, PerspectiveCamera, Vector3}
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import vongrid.controls._

import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}


object NsTouchControls extends TouchControls {
  override val ORBIT = Fingers.THREE
  override val PAN = Fingers.ONE
}


object NsMouseControls extends MouseControls {
  override val ORBIT = Mouse.RIGHT
  override val PAN = Mouse.LEFT
}

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
@ScalaJSDefined
class NSOrbitControls(camera: Camera, element: HTMLElement) extends OrbitControlsPort(camera, element, NsMouseControls, NsTouchControls) {

}
