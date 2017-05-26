package wiki

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.{SearchHit, SearchHits}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Future

class WikiRoutesTest extends FunSuite
  with Matchers with ScalatestRouteTest with MockFactory {

  def mockPageRepo(page: String): WikiRepository = {
    class GetResponseWrapper extends GetResponse(null)
    val validResponse = stub[GetResponseWrapper]
    (validResponse.isExists _).when().returns(true)
    (validResponse.getSourceAsString _).when().returns(page)

    val invalidResponse = stub[GetResponseWrapper]
    (invalidResponse.isExists _).when().returns(false)

    val esStub = stub[ElasticSearchConf]
    (esStub.client _).when().returns(null)

    class WikiRepositoryWrapper extends WikiRepository(esStub)
    val repoStub = stub[WikiRepositoryWrapper]
    (repoStub.get _).when(1).returns(Future(validResponse))
    (repoStub.get _).when(2).returns(Future(invalidResponse))

    repoStub
  }

  test("GET request for valid page returns JSON with page data") {
    val page = """{"title":"x","text":"y"}"""
    val repo = mockPageRepo(page)
    val routes = new WikiRoutes(repo).routes

    Get("/page/1") ~> routes ~> check {
      responseAs[String] shouldEqual page
    }
  }

  test("GET request for invalid page returns error information") {
    val page = """{"title":"x","text":"y"}"""
    val repo = mockPageRepo(page)
    val routes = new WikiRoutes(repo).routes

    Get("/page/2") ~> routes ~> check {
      responseAs[String] should include ("resource could not be found")
    }
  }

  def mockPagesRepo(pages: Array[java.util.Map[String, AnyRef]]): WikiRepository = {
    val pageStubs = pages.map(s => {
      val hit = stub[SearchHit]
      (hit.getSource _).when().returns(s)
      hit
    })
    class SearchHitsWrapper extends SearchHits(null, 0, 0)
    val hits = stub[SearchHitsWrapper]
    (hits.getHits _).when().returns(pageStubs)

    val response = stub[SearchResponse]
    (response.getHits _).when().returns(hits)

    val esStub = stub[ElasticSearchConf]
    (esStub.client _).when().returns(null)

    class WikiRepositoryWrapper extends WikiRepository(esStub)
    val repoStub = stub[WikiRepositoryWrapper]
    (repoStub.search _).when("x", None, None).returns(Future(response))
    (repoStub.search _).when("x", Some(1), None).returns(Future(response))
    (repoStub.search _).when("x", None, Some(1)).returns(Future(response))
    (repoStub.search _).when("x", Some(1), Some(2)).returns(Future(response))

    repoStub
  }

  test("GET request for pages with query string specified") {
    val page = new java.util.HashMap[String, AnyRef]()
    page.put("title", "x")
    page.put("text", "y")
    val repo = mockPagesRepo(Array(page, page))
    val routes = new WikiRoutes(repo).routes

    val expected = """[{"text":"y","title":"x"},{"text":"y","title":"x"}]"""

    Get("/pages?q=x") ~> routes ~> check {
      responseAs[String] shouldEqual expected
    }
  }

  test("GET request for pages with query string and from specified") {
    val page = new java.util.HashMap[String, AnyRef]()
    page.put("title", "x")
    page.put("text", "y")
    val repo = mockPagesRepo(Array(page, page))
    val routes = new WikiRoutes(repo).routes

    val expected = """[{"text":"y","title":"x"},{"text":"y","title":"x"}]"""

    Get("/pages?q=x&from=1") ~> routes ~> check {
      responseAs[String] shouldEqual expected
    }
  }

  test("GET request for pages with query string and size specified") {
    val page = new java.util.HashMap[String, AnyRef]()
    page.put("title", "x")
    page.put("text", "y")
    val repo = mockPagesRepo(Array(page, page))
    val routes = new WikiRoutes(repo).routes

    val expected = """[{"text":"y","title":"x"},{"text":"y","title":"x"}]"""

    Get("/pages?q=x&size=1") ~> routes ~> check {
      responseAs[String] shouldEqual expected
    }
  }

  test("GET request for pages with query string, from and size specified") {
    val page = new java.util.HashMap[String, AnyRef]()
    page.put("title", "x")
    page.put("text", "y")
    val repo = mockPagesRepo(Array(page, page))
    val routes = new WikiRoutes(repo).routes

    val expected = """[{"text":"y","title":"x"},{"text":"y","title":"x"}]"""

    Get("/pages?q=x&from=1&size=2") ~> routes ~> check {
      responseAs[String] shouldEqual expected
    }
  }

}
