package nosketch

import paperjs.Basic.Point

// A simple implementation of pan and zoom: just change the
  // zoom factor or the view center.
object SimplePanAndZoom {


  // The zoom and pan logic is extracted into functions that
  // produce the new zoom (or center) given the old zoom (or center) and mouse wheel deltas.

  // Compute the new zoom factor from the old
  // zoom factor and some delta that is given to us by the mousewheel plugin.
  def changeZoom(oldZoom: Double, delta: Double): Double = {
    val factor = 1.05
    if(delta < 0)
      oldZoom * factor
    else if (delta > 0)
      oldZoom / factor
    else
      oldZoom
  }

  // Compute the new center from old center and the delta given by the mousewheel plugin.
  def changeCenter(oldCenter: Point, deltaX: Double, deltaY: Double, factor: Double) = {
    val offset = new Point(deltaX, -deltaY)
    oldCenter.add(offset.multiply(factor))
  }


  // <a name="Example1"></a>Example 1
  // --------------------------------

  // Here is the code that produces the example above. Feel free to skip ahead to
  // the [stable zoom](//StableZoom).

  // Draw a grid with major and minor lines

//  example1 = (canvasID) ->
//    // Setup the `paper` object
//    canvas = document.getElementById(canvasID)
//  paper.setup canvas
//    // Remember the current view so we can access it in event handlers.
//    view = paper.view
//
//  // Create a grid and a circle.
//  width = 600
//  height = 300
//  drawGrid width, height
//  new paper.Path.Circle center:[100, 100], radius: 20, fillColor: 'green '
//  box = new paper.Path.Rectangle from:[0, 0], to:[10, 10], fillColor: 'gray '
//  box.position = view.center
//
//  // Use a `SimplePanAndZoom` to translate from mouse events to changes in the view.
//  panAndZoom = new SimplePanAndZoom()
//
//  // We use the jquery-mousewheel plugin to get the events.
//  $("////{canvasID}").mousewheel(event) ->
//  if event.shiftKey
//  view.center = panAndZoom.changeCenter view.center, event.deltaX, event.deltaY, event.deltaFactor
//  event.preventDefault()
//  else if event.altKey
//  view.zoom = panAndZoom.changeZoom view.zoom, event.deltaY
//  event.preventDefault()
//
//
//  // When using paper.js from javascript directly, you have to call
//  // `view.draw()` to draw the scene.
//  view.draw()
}