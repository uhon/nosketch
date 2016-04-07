package nosketch.components

import nosketch.viewport.ViewPort
;

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
trait ZoomAwareObject {
    def redraw(viewport: ViewPort)
}
