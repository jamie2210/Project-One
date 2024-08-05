package services

import connectors.LibraryConnector
import models._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT

@Singleton
class LibraryService @Inject()(connector: LibraryConnector){

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, Book] =
    connector.get[Book](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))
}
