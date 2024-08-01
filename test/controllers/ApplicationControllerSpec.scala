package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import org.scalamock.clazz.MockImpl.mock
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import repositories.DataRepository

import scala.Option.when
import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication{

  val TestApplicationController = new ApplicationController(
    component,
    repository
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
    "return BadRequest when no data is found" in {
      beforeEach()
      // Create request and ensure the item is created
      val createRequest: FakeRequest[JsValue] = buildPost(s"/api${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)
      status(createdResult) shouldBe Status.CREATED

      // Delete the book
      val deleteResult: Future[Result] = TestApplicationController.delete(dataModel._id)(FakeRequest(DELETE, dataModel._id))
      // Attempt to read book
      val readResult: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())

      status(readResult) shouldBe Status.NOT_FOUND
      contentAsJson(readResult).as[String] shouldBe "Item not found"
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
    "return NotFound for a non-existent book id" in {
      beforeEach()
      // Request for a non-existent book ID
      val nonExistentId = "non-existent-id"
      val readResult: Future[Result] = TestApplicationController.read(nonExistentId)(FakeRequest())

      status(readResult) shouldBe Status.NOT_FOUND
      contentAsJson(readResult).as[String] shouldBe "Item not found"
      afterEach()
    }
  }

  "Application .update"  should {
    "update a book in the database by id" in {
      beforeEach()
      // Create request and ensure the item is created
      val createRequest: FakeRequest[JsValue] = buildPost(s"/api${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)
      status(createdResult) shouldBe Status.CREATED

      // Update the created item
      val updateRequest: FakeRequest[JsValue] = FakeRequest(PUT, s"/api/update${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val updateResult: Future[Result] = TestApplicationController.update(dataModel._id.toString)(updateRequest)


      status(updateResult) shouldBe Status.ACCEPTED
      contentAsJson(updateResult).as[DataModel] shouldBe dataModel

      afterEach()
    }
    "return NotFound for a non-existent book id" in {
      beforeEach()
      // Request for a non-existent book ID
      val nonExistentId = "non-existent-id"
      val readResult: Future[Result] = TestApplicationController.read(nonExistentId)(FakeRequest())

      status(readResult) shouldBe Status.NOT_FOUND
      contentAsJson(readResult).as[String] shouldBe "Item not found"
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
    }
}
