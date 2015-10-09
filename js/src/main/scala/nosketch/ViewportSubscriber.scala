package nosketch

import paperjs.Basic.Point

trait ViewportSubscriber {
  def onZoom: Unit
  def onScale: Unit
}