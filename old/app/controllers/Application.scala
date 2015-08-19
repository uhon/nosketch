package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index.render())
  }

  def draw = Action {
    Ok(views.html.draw.render())
  }

}