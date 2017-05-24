package wiki

import scala.io.Source
import scala.xml.pull._

case class Page(id: Int, title: String, text: String)

class Parser(src: Source) extends Iterator[Page] {

  private val reader = new XMLEventReader(src)

  def hasNext: Boolean = reader.exists {
    case EvElemStart(_, "page", _, _) => true
    case _ => false
  }

  def next: Page = {
    val title = getElement("title")
    val id = getElement("id")
    val text = getElement("text")
    Page(id.toInt, title, text)
  }

  private def getElement(element: String) = {
    import scala.collection.mutable
    def go(acc: Option[mutable.StringBuilder]): String = (reader.next, acc) match {
      case (EvElemStart(_, elem, _, _), _) if elem == element =>
        go(Some(new mutable.StringBuilder))
      case (EvElemEnd(_, elem), Some(buf)) if elem == element =>
        buf.toString
      case (EvText(text), Some(buf)) =>
        go(Some(buf ++= text))
      case (EvEntityRef(entity), Some(buf)) =>
        go(Some(buf ++= s"&$entity;"))
      case _ =>
        go(acc)
    }
    go(None)
  }

}
