package nosketch

import paperjs.Paths.Path

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait NosketchObject {
  def redraw(scaleFactor: Double)
  def getPath : Path
}
