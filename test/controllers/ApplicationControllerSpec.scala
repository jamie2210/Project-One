package controllers

import baseSpec.BaseSpecWithApplication
import models._
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component,
    service,
    repository,
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())


  "ApplicationController .index()" should {
    "Display books" in{
      beforeEach()
      // Create request and ensure the item is created
      val createRequest: FakeRequest[JsValue] = buildPost(s"/api${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)
      status(createdResult) shouldBe Status.CREATED

      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe OK

    }
    "return APIError when no data is found" in {
      beforeEach()
      // Create request and ensure the item is created
      val createRequest: FakeRequest[JsValue] = buildPost(s"/api${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)
      status(createdResult) shouldBe Status.CREATED
      val errorResponse = APIError.BadAPIResponse(404, "Book cannot be found")

      // Delete the book
      val deleteResult: Future[Result] = TestApplicationController.delete(dataModel._id)(FakeRequest(DELETE, dataModel._id))
      // Attempt to read book
      val readResult: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())

      status(readResult) shouldBe errorResponse.httpResponseStatus
      contentAsJson(readResult) shouldBe Json.toJson(errorResponse.upstreamMessage)
      afterEach()
    }
  }


  "ApplicationController .create" should {
    "create a book in the database" in {
      beforeEach()
      // Create request and ensure the item is created
      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
      afterEach()
    }
    "return a left if an error occurs" in {
      beforeEach()
      afterEach()
    }
    "return BadRequest for invalid JSON" in {
      beforeEach()
      val invalidJson: JsValue = Json.parse("""{ "invalid": "json" }""")
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody[JsValue](invalidJson)
      val result: Future[Result] = TestApplicationController.create()(request)

      status(result) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  "ApplicationController .read" should {
    "find a book in the database by id" in {
      beforeEach()
      // Create request and ensure the item is created
      val request: FakeRequest[JsValue] = buildGet(s"/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
      afterEach()
    }
    "return APIError for a non-existent book id" in {
      beforeEach()
      // Request for a non-existent book ID
      val nonExistentId = "non-existent-id"
      val readResult: Future[Result] = TestApplicationController.read(nonExistentId)(FakeRequest())
      val errorResponse = APIError.BadAPIResponse(404, "Book cannot be found")

      status(readResult) shouldBe errorResponse.httpResponseStatus
      contentAsJson(readResult) shouldBe Json.toJson(errorResponse.upstreamMessage)
      afterEach()
    }
  }


  "ApplicationController .update" should {

    "update a book in the database" in {
      beforeEach()
      val initialBook = DataModel("1", "Initial Book", "Initial description", 100)
      await(repository.create(initialBook))

      val updatedBook = DataModel("1", "Updated Book", "Updated description", 150)
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](Json.toJson(updatedBook))
      val updatedResult: Future[Result] = TestApplicationController.update("1")(request)

      status(updatedResult) shouldBe Status.ACCEPTED
      contentAsJson(updatedResult) shouldBe Json.toJson(updatedBook)
      afterEach()
    }

    "return 404 Not Found when trying to update a non-existent book" in {
      beforeEach()
      val updatedBook = DataModel("1", "Updated Book", "Updated description", 150)
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](Json.toJson(updatedBook))
      val updatedResult: Future[Result] = TestApplicationController.update("1")(request)

      status(updatedResult) shouldBe Status.NOT_FOUND
      afterEach()
    }

    "return BadRequest for invalid JSON" in {
      beforeEach()
      val invalidJson: JsValue = Json.parse("""{ "invalid": "json" }""")
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](invalidJson)
      val result: Future[Result] = TestApplicationController.update("1")(request)

      status(result) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }



  "ApplicationController .delete()" should {
    "delete a book in the database by id" in {
      beforeEach()
      // Create request and ensure the item is created
      val createRequest: FakeRequest[JsValue] = buildPost(s"/api${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)
      status(createdResult) shouldBe Status.CREATED

      // Delete created item
      val deleteRequest: FakeRequest[AnyContentAsEmpty.type ] = FakeRequest(DELETE, s"/api/delete${dataModel._id}")
      val deleteResult: Future[Result] = TestApplicationController.delete(dataModel._id)(deleteRequest)

      status(deleteResult) shouldBe Status.ACCEPTED
      afterEach()
      }
    "return error response when book cannot be found" in {
      // Request for a non-existent book ID
      beforeEach()
      val nonExistentId = "non-existent-id"
      val readResult: Future[Result] = TestApplicationController.read(nonExistentId)(FakeRequest())
      val errorResponse = APIError.BadAPIResponse(200, "Book cannot be found")

      status(readResult) shouldBe errorResponse.httpResponseStatus
      contentAsJson(readResult) shouldBe Json.toJson(errorResponse.upstreamMessage)
      afterEach()
    }
    }
}
