package controllers
import models._
import org.mongodb.scala.result.UpdateResult
import play.api.libs.json
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}
import repositories.DataRepository
import services.LibraryService
import views.js.helper.json

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents, val service: LibraryService,
                                       val dataRepository: DataRepository)(implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(_) => BadRequest(Json.toJson("Unable to find any books"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(book, _) =>
        dataRepository.create(book).map {
          case Right (createdBook) => Created({Json.toJson(createdBook)})
          case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.upstreamMessage))
        }
      case JsError(_) => Future(BadRequest(Json.toJson("Invalid Json format")))
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Right(item) => Ok{Json.toJson(item)}
      case Left (_) => NotFound(Json.toJson("Item not found"))
    }
  }

    def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
          dataRepository.update(id, dataModel).flatMap {_ =>
            dataRepository.read(id).map {
              case Right(updatedItem) => Accepted({Json.toJson(updatedItem)})
              case Left(_) => NotFound(Json.toJson(s"Item $id not found"))
              }
            }
          case JsError(_) => Future.successful(BadRequest)
      }
    }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.delete(id).map {
      case Right(result) =>
      if (result.getDeletedCount > 0) {
        Accepted(Json.toJson("Item deleted"))
      } else {
        NotFound(Json.toJson("Item not found"))
      }
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.upstreamMessage))
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).value.map {
      case Right(book) => Ok(Json.toJson(search, term))
      case Left(error) => Status(error.httpResponseStatus)
    }
  }

}


