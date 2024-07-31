package controllers
import models.DataModel
import org.mongodb.scala.result.UpdateResult
import play.api.libs.json
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}
import repositories.DataRepository
import views.js.helper.json

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

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(book, _) =>
        dataRepository.create(book).map(created
        => Accepted{Json.toJson(created)})
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Some(item) => Ok{Json.toJson(item)}
      case None => NotFound(Json.toJson("Item not found"))
    }
  }

    def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
          dataRepository.update(id, dataModel).flatMap {_ =>
            dataRepository.read(id).map {
              case updatedItem => Accepted({Json.toJson(updatedItem)})
              case _ => NotFound(Json.toJson(s"Item $id not found"))
              }
            }
          case JsError(_) => Future.successful(BadRequest)
      }
    }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.delete(id).map { result =>
      if (result.getDeletedCount > 0) {
        Accepted
      } else {
        NotFound(Json.toJson("Item not found"))
      }
    }
  }

}


