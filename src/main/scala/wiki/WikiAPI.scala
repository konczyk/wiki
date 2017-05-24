package wiki

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object WikiAPI extends App with WikiService {

  implicit val system = ActorSystem("wki-api")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val conf = ConfigFactory.load().getConfig("akka")
  val host = conf.getString("host")
  val port = conf.getInt("port")

  val route : Route = {
    (path("get"/IntNumber) & get) {
      getPage(_) match {
        case Some(page) => complete(page)
        case None => complete(StatusCodes.NotFound)
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)
  println(s"Wiki API running at http://$host:$port\nHit RETURN to terminate")
  StdIn.readLine()

  bindingFuture.flatMap(_.unbind())
  system.terminate()

}
