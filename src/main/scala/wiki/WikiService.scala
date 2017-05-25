package wiki

import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

class WikiService(repo: WikiRepository) {

  def getPage(id: Int): Option[String] = {
    val response = repo.get(id)
    if (response.isExists) {
      val page = pretty(render(
        ("title", response.getSource.get("title").toString) ~
        ("text", response.getSource.get("text").toString)
      ))
      Some(page)
    } else None
  }

}
