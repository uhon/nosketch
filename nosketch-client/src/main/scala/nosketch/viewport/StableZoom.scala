package nosketch.viewport

import nosketch.SimplePanAndZoom
import paperjs.Basic.Point
import org.scalajs.dom._

/**
 * Try zooming in on the circle (again shift-mousewheel moves the box around,
 * alt-mousewheel zooms in and out). The drawing zooms in around your mouse pointer.
 *
 * <canvas id="paper2" width="600" height="300" style="background: gray;"></canvas>
 *
 * Let's derive the formula for this systematically.
 * The default paper.js zoom has the view's center as a fixed point.
 * Write the default zoom transform as some function $Z$ and call the view's center point $c$.
 * Then we have
 * $$Z(c) = c$$
 * We want to apply the default zoom and then correct it by a translation that makes sure
 * the point under the mouse $p$ stays where it is.
 * We are looking for a translation vector $a$ such that
 * $$Z(p) + a = p$$
 * This means that the correction has to be $a = p - Z(p)$.
 *
 * How do we get a formula for the default zoom transform $Z$?
 * It is a scaling that has the view center $c$ as a fixed point.
 * This can be done by shifting $c$ to the origin, then scaling by a factor $\beta$ then shifting back to $c$:
 * $$Z(x) = \beta \cdot (x - c) + c$$
 * You can check that indeed $Z(c) = c$.
 *
 * With that our correction becomes
 * $$a = p - Z(p) = p - \beta \cdot (p - c) - c$$
 *
 * Make a subclass of `SimplePanAndZoom` for stable zooming.

 **/


object StableZoom {

  def changeZoom(oldZoom: Double, delta: Double, c: Point, p: Point): (Double,Point) = {
    console.info(oldZoom, delta, c, p)
    val newZoom = SimplePanAndZoom.changeZoom(oldZoom, delta)
    val beta = oldZoom / newZoom
    val pc = p.subtract(c)
    val a = p.subtract(pc.multiply(beta)).subtract(c)

    // console.log("zoom and offset", newZoom, a)
    (newZoom, a)

  }
}
