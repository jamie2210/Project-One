package controllers
import models.DataModel
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext


@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       val dataRepository: DataRepository)(implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
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

