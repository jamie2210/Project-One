package services

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models._
import models.Book
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{

  val mockConnector = mock[LibraryConnector]
  implicit val executiveContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "InfoDump" -> Json.obj(
      "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
    )
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.right(Future.successful(gameOfThrones.as[Book])))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Right(gameOfThrones.as[Book])
      }
    }
    "return an error" in {
      val url: String = "testUrl"

      (mockConnector.get[Book](_: String)(_: OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.left(Future.successful(APIError.BadAPIResponse(404, "Book not found"))))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Left(APIError.BadAPIResponse(404, "Book not found"))
      }
    }
  }

}

