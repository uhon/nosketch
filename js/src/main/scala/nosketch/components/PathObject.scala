package nosketch.components

import paperjs.Paths.Path
import paperjs.Basic.Point

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait PathObject extends NosketchObject {
  def getPath : Path
}
