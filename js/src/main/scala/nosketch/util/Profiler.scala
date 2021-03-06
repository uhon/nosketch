package nosketch.util

import nosketch.hud.elements.debug.SimpleIndicator
import nosketch.hud.{DebugHUD, DebugHUDElement}
import org.scalajs.dom._

import scala.collection.mutable
/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object Profiler {

  val indicators = mutable.Map[String, DebugHUDElement]()



  def formatNumber(number: String, distance: Int = 3): String = {
    val nArray = number.toCharArray.reverse
    var result = ""
    for(i <- 0 to number.length) {
      if(i != 0 && i != number.length && i % distance == 0) {
        result = "'" + result
      }
      result = nArray(i) + result
    }
    result
  }

  def reportDuration(task: String, startTime: Long): Unit = {

    val duration = System.currentTimeMillis() - startTime
      if(!indicators.contains(task)) {
        indicators += task -> new SimpleIndicator(task)
        DebugHUD.addElement(indicators(task))
      }

      indicators(task).setValue(duration)
      //console.warn(task + " took: " + formatNumber(duration.toString) + "ns")
  }
}
