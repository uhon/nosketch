package nosketch.loading
import nosketch.io.ImageUrls
import nosketch.shared.util.FA
import nosketch.shared.util.FA._
import org.denigma.threejs.{LoadingManager, Texture, TextureLoader}

import scala.annotation.tailrec
import scala.scalajs.js.timers._
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, _}

/**
  * @author Urs Honegger &lt;u.honegger@insign.ch&gt;
  */
object NSTextureLoader {

  val lm = new LoadingManager()
  val tl = new TextureLoader(lm)
  var preloaded = Map[FA, Texture]()

  def load(callback: (Map[FA, Texture]) => Unit) = {
    var fontAwesome = FA.values.zipWithIndex.toList

    def incLoad(i: Int, acc: Map[FA, Texture], reportBack: (Map[FA, Texture]) => Unit): Unit = {
       // FIXME: only subset is loaded
      if(i < 20) {
        val url = ImageUrls.pngShape(fontAwesome(i)._1.toString)
        console.log("loading " + url)
        // FIXME: How to handle if resource unavailable
        lm.onError = () => incLoad(i+1, acc, reportBack)
        tl.load(
          url,
          (tex: Texture) => {
            val newAcc = acc.+(fontAwesome(i)._1 -> tex)
            incLoad(i+1, newAcc, reportBack)
          }
        )
      } else {
        reportBack(acc)
      }
    }

    incLoad(0, Map(), callback)
  }
}
