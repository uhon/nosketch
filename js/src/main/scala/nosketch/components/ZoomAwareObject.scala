package nosketch.components

import nosketch.viewport.ViewPort
import paperjs.Basic.Point

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait ZoomAwareObject {
    def redraw(viewport: ViewPort)
}
