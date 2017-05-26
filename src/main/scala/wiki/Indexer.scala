package wiki

object Indexer extends App {

  val parser = new Parser(scala.io.Source.fromFile(args(0)))
  val conf = new ElasticSearchConf()
  val wiki = new WikiRepository(conf)

  private def index(pages: Iterator[Page], acc: Int): Int = {
    val data = pages.take(5000).toList
    if (data.isEmpty) acc
    else {
      val response = wiki.index(data)
      if (response.hasFailures)
        println(response.buildFailureMessage())
      index(pages, acc + data.length)
    }
  }

  val indexed = index(parser, 0)
  println(s"Indexed $indexed pages")
  conf.client.close()
  parser.close()
}
