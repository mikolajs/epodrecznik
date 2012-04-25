
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Edit(user:String, date:String)


object Theme extends MongoDocumentMeta[Theme] {
  override def collectionName = "subjectSlajds"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Theme(ObjectId.get, false, Nil, "", "","" , "", "Empty", null ,"")
}
case class Theme(var _id: ObjectId,  var confirmed:Boolean, var edit: List[Edit], 
				var title: String, var department: String, var referTo:String,
				var moderator: String, var subjectInfo: String, var subjectId:ObjectId, var slides: String) 
				 extends MongoDocument[Theme] {
  def meta = Theme
}