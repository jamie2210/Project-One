package controllers

import baseSpec.BaseSpecWithApplication

import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._

class ApplicationControllerSpec extends BaseSpecWithApplication{

  val TestApplicationController = new ApplicationController(
    component
  )

  "ApplicationController .index()" should {

    val result = TestApplicationController.index()(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }

  }

  "ApplicationController .create()" should {

    val result = TestApplicationController.create()(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .read()" should {

    val result = TestApplicationController.read("String")(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .update()" should {


    val result = TestApplicationController.update("String")(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .delete()" should {


    val result = TestApplicationController.delete("String")(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }
  }

}
