package controllers
import models.DataModel
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import repositories.DataRepository


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


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

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Some(item) => Ok{Json.toJson(item)}
      case None => NotFound(Json.obj("error" -> "Item not found"))
    }
  }

  def update(id: String) = Action {
    Ok("update test!")

   }

  def delete(id: String) = Action {
    Ok("update test!")
  }

}


