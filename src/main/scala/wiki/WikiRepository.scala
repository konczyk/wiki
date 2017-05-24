package wiki

import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

trait WikiRepository {

  val index: String
  val client: Client
  val esType = "page"

  def index(pages: List[Page]): BulkResponse = {
    val bulkRequest = client.prepareBulk()
    pages.foreach { page =>
      bulkRequest.add(
        client.prepareIndex(index, esType, page.id)
              .setSource(pageToJson(page)))
    }
    bulkRequest.get()
  }

  def pageToJson(page: Page): String =
    compact(render(("title", page.title) ~ ("text", page.text)))

}

