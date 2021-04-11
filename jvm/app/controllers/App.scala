package controllers

import nosketch.shared.SharedMessages
import play.api.mvc.Results.Ok
import play.mvc.Controller
import play.api.mvc.{AbstractController, Action, BaseController, ControllerComponents}

import javax.inject.Inject

class App  @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action { request =>
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def draw = Action { request =>
    Ok(views.html.draw.render("Draw your Sketch now!"))
  }

}
