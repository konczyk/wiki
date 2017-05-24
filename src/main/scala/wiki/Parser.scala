package wiki

import scala.io.Source
import scala.xml.pull._

case class Page(title: String, text: String)

class Parser(src: Source) extends Iterator[Page] {

  private val reader = new XMLEventReader(src)

  def hasNext: Boolean = forward(reader)
  def next: Page = Page(getTitle(reader), getText(reader))

  private def forward(reader: Iterator[XMLEvent]) =
    reader.exists {
      case EvElemStart(_, "page", _, _) => true
      case _ => false
    }

  private def getTitle(reader: Iterator[XMLEvent]) =
    getElement(reader, "title")

  private def getText(reader: Iterator[XMLEvent]) =
    getElement(reader, "text")

  private def getElement(reader: Iterator[XMLEvent], element: String) = {
    import scala.collection.mutable
    def go(reader: Iterator[XMLEvent], acc: Option[mutable.StringBuilder]): String =
      (reader.next, acc) match {
        case (EvElemStart(_, elem, _, _), _) if elem == element =>
          go(reader, Some(new mutable.StringBuilder))
        case (EvElemEnd(_, elem), Some(buf)) if elem == element =>
          buf.toString
        case (EvText(text), Some(buf)) =>
          go(reader, Some(buf ++= text))
        case (EvEntityRef(entity), Some(buf)) =>
          go(reader, Some(buf ++= s"&$entity;"))
        case _ =>
          go(reader, acc)
      }
    go(reader, None)
  }

}
