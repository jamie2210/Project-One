package controllers

import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index()= Action {
    Ok("index test!")
  }
  def create() = Action {
    Ok("create test!")
  }
  def read(id: String) = Action {
    Ok("read test!")
  }
  def update(id: String) = Action {
    Ok("update test!")
  }
  def delete(id: String) = Action {
    Ok("update test!")
  }

}

