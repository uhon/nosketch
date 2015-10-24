package nosketch.util

import org.scalajs.dom._

/**
 * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
 */
object Profiler {
  val treshhold = 1000000 // everything below that value is not reported
  
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
    val duration = System.nanoTime - startTime
    if(duration > treshhold) {
      console.warn(task + " took: " + formatNumber(duration.toString) + "ns")
    }
  }
}
