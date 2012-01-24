
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

//case class Slajd(var nr: Int, var title: String, var img: String, var text: String, var layout: Char)

object Theme extends MongoDocumentMeta[Theme] {
  override def collectionName = "subjectSlajds"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Theme(ObjectId.get, false, "", "", "", "", "", "", "", Nil)
}
case class Theme(var _id: ObjectId, var confirmed: Boolean, var referTo: String,
  var moderator: String, var author: String, var title: String, var department: String,
  var date: String, var subject: String, var slides: List[String])
  extends MongoDocument[Theme] {
  def meta = Theme
  def slidesListString = {
    "[ " + slides.map(s => "'%s'".format( s)).mkString(",") + "]"
  }
}