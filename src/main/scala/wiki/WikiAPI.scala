package wiki

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object WikiAPI extends App {

  implicit val system = ActorSystem("wki-api")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val conf = ConfigFactory.load().getConfig("akka")
  val host = conf.getString("host")
  val port = conf.getInt("port")

  val esConf = new ElasticSearchConf()
  val repo = new WikiRepository(esConf)
  val routes = new WikiRoutes(repo).routes

  val bindingFuture = Http().bindAndHandle(routes, "localhost", 9000)

  println(s"Wiki API running at http://$host:$port\nHit RETURN to terminate")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
