package controllers
import models.{Book, DataModel, InfoDump}
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
        dataRepository.create(book).map(created
        => Created{Json.toJson(created)})
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
    dataRepository.delete(id).map { result =>
      if (result.getDeletedCount > 0) {
        Accepted
      } else {
        NotFound(Json.toJson("Item not found"))
      }
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).map {
      case Book(search) => Ok{Json.toJson(Book(search))}
      case Book(search) => Ok{Json.toJson(Book(search))}
      case _ => NotFound(Json.toJson("Item not found"))
    }
  }

}


