package wiki

import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

trait WikiService {

  val db = new WikiRepository with ElasticSearchConf

  def getPage(id: Int): Option[String] = {
    val response = db.get(id)
    if (response.isExists) {
      val page = pretty(render(
        ("id", response.getId) ~
        ("title", response.getSource.get("title").toString) ~
        ("text", response.getSource.get("text").toString)
      ))
      Some(page)
    } else None
  }

}
