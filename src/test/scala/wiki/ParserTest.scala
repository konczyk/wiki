package wiki

import org.scalatest.FunSuite

class ParserTest extends FunSuite {

  test("empty source produces no pages") {
    val src = scala.io.Source.fromString("")
    val parser = new Parser(src)

    assert(parser.hasNext === false)
    assert(parser.size === 0)
  }

  test("single page is extracted") {
    val src = scala.io.Source.fromString(
      "<xml><x>vae</x><page><title>x</title><id/><text>y</text></page><y/></xml>")
    val parser = new Parser(src)
    assert(parser.hasNext === true)
    assert(parser.next === Page("x", "y"))
    assert(parser.hasNext === false)
  }

  test("two pages are extracted") {
    val src = scala.io.Source.fromString(
      """<xml><x>vae</x><page><title>x</title><id/><text>y</text></page><y/>
        |<page><title>w</title><text>z</text></page></xml>""".stripMargin)
    val parser = new Parser(src)
    assert(parser.hasNext === true)
    assert(parser.next === Page("x", "y"))
    assert(parser.hasNext === true)
    assert(parser.next === Page("w", "z"))
    assert(parser.hasNext === false)
  }

  test("html entities are included") {
    val src = scala.io.Source.fromString(
      "<xml><page><title>x</title><text>y &amp;nbsp; &lt;code/&gt;</text></page></xml>")
    val parser = new Parser(src)
    assert(parser.hasNext === true)
    assert(parser.next === Page("x", "y &amp;nbsp; &lt;code/&gt;"))
  }

}
