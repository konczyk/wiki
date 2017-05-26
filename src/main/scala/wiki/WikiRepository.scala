package wiki

import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.index.query.{Operator, QueryBuilders}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

import scala.concurrent.Future

class WikiRepository(private val conf: ElasticSearchConf) {

  private val dbIndex = conf.index
  private val dbClient = conf.client
  private val maxHits = conf.maxHits
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

  def get(id: Int): Future[GetResponse] = {
    val request = dbClient.prepareGet(dbIndex, repoType, id.toString)
    RequestExecutor[GetResponse]().execute(request)
  }

  def search(q: String, from: Option[Int], size: Option[Int]): Future[SearchResponse] = {
    val request = dbClient.prepareSearch(dbIndex).setTypes(repoType)
    request.setQuery(QueryBuilders.matchQuery("text", q).operator(Operator.AND))
    from.foreach(x => request.setFrom(x))
    size.foreach(x => request.setSize(math.min(x, maxHits)))

    RequestExecutor[SearchResponse]().execute(request)
  }

  private def pageToJson(page: Page): String =
    compact(render(("title", page.title) ~ ("text", page.text)))

}

