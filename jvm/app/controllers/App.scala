package controllers

import nosketch.shared.SharedMessages
import play.api.mvc.Results.Ok
import play.mvc.Controller
import play.mvc.Action
import play.api._
import play.api.mvc._

class App extends Controller {

  def index = Action { request =>
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def draw = Action { request =>
    Ok(views.html.draw.render("Draw your Sketch now!"))
  }

}
