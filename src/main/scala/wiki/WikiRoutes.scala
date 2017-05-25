package wiki

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.util.{Failure, Success}

import org.json4s.native.Serialization.write
import org.json4s.native.JsonMethods.parse

case class WikiRoutes(repo: WikiRepository) {

  implicit val formats = org.json4s.DefaultFormats

  def routes: Route = {
    (path("get"/IntNumber) & get) { id =>
      onComplete(repo.get(id)) {
        case Success(response) =>
          if (response.isExists)
            complete(response.getSourceAsString)
          else
            complete(StatusCodes.NotFound)
        case Failure(_) =>
          complete(StatusCodes.InternalServerError)
      }
    } ~
    (path("search") & parameters("q", "from".as[Int].?, "size".as[Int].?)) { (q, from, size) =>
      onComplete(repo.search(q, from, size)) {
        case Success(response) =>
          val hits = response.getHits.getHits
          complete(write(hits.map(doc => parse(doc.getSourceAsString))))
        case Failure(_) =>
          complete(StatusCodes.InternalServerError)
      }
    }
  }

}
