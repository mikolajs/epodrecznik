
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object Theme extends MongoDocumentMeta[Theme] {
  override def collectionName = "subjectSlajds"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Theme(ObjectId.get, false, "", "", "", "", "", "", "", "")
}
case class Theme(var _id: ObjectId, var confirmed: Boolean,
  var moderator: String, var author: String, var title: String, var department: String,
  var dateAdd: String, var dateEdit: String, var subject: String, var slides: String)
  extends MongoDocument[Theme] {
  def meta = Theme
}