package models

import play.api.libs.json.{Json, OFormat}

case class InfoDump(
                       _id: String,
                       name: String,
                       description: String,
                       pageCount: Int
                     )

object InfoDump {
  val format: OFormat[InfoDump] = Json.format[InfoDump]
}