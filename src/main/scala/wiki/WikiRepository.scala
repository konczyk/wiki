package wiki

import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.get.GetResponse
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

class WikiRepository(conf: ElasticSearchConf) {

  private val dbIndex = conf.index
  private val dbClient = conf.client
  private val repoType = "page"

  def index(pages: List[Page]): BulkResponse = {
    val bulkRequest = dbClient.prepareBulk()
    pages.foreach { page =>
      bulkRequest.add(
        dbClient.prepareIndex(dbIndex, repoType, page.id)
              .setSource(pageToJson(page)))
    }
    bulkRequest.get()
  }

  def get(id: Int): GetResponse =
    dbClient.prepareGet(dbIndex, repoType, id.toString).get()

  private def pageToJson(page: Page): String =
    compact(render(("title", page.title) ~ ("text", page.text)))

}

