package models

import play.api.libs.json.{Json, OFormat}

case class Book(id: String, InfoDump: InfoDump)

object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}
