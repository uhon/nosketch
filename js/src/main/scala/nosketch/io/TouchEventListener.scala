package nosketch.io

import org.scalajs.dom._
import org.scalajs.dom.raw.MouseEvent
import paperjs.Tools.ToolEvent
import paperjs.Basic.Point

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
trait TouchEventListener {
   def onZoom(delta: Double, touchCenter: Point): Unit = {}

   def onDrag(eventPoint: Point): Unit = {}
 }
