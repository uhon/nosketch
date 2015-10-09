package controllers

import play.api.mvc._
import nosketch.SharedMessages

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def draw = Action {
    Ok(views.html.draw.render("Draw your Sketch now!"))
  }

}
