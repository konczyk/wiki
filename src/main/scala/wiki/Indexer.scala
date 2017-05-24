package wiki

object Indexer extends App {

  val parser = new Parser(scala.io.Source.fromFile(args(0)))
  val wiki = new WikiRepository with ElasticSearchConf

  def go(pages: Iterator[Page], acc: Int): Int = {
    val data = pages.take(5000).toList
    if (data.isEmpty) acc
    else {
      val response = wiki.index(data)
      if (response.hasFailures)
        println(response.buildFailureMessage())
      go(pages, acc + data.length)
    }
  }

  val indexed = go(parser, 0)
  println(s"Indexed $indexed pages")
  wiki.client.close()
}
