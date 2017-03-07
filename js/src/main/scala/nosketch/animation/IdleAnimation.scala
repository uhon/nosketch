package nosketch.animation

import nosketch.components.VisibleHexagon


object IdleAnimation extends Animation {
  val startTime = System.currentTimeMillis()

  override def animationLoop: Boolean = {
    false
  }
}