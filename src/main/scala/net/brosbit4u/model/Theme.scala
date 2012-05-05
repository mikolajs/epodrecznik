
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Edit(user:String, date:String)

//authors - latest is older
object Theme extends MongoDocumentMeta[Theme] {
  override def collectionName = "slides"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Theme(ObjectId.get, false, Nil, "", "" ,null, "" , "" ,"Empty", 0,null ,"")
}
case class Theme(var _id: ObjectId,  var confirmed:Boolean, var authors: List[Edit], 
				var title: String, var departmentInfo: String, var departmentId:ObjectId, var referTo:String,
				var moderator: String, var subjectInfo: String, var subjectLev:Int, var subjectId:ObjectId, var slides: String) 
				 extends MongoDocument[Theme] {
  def meta = Theme
}