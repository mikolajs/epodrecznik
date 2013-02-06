
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Edit(user:String, date:String)

//authors - last is older
object Slide extends MongoDocumentMeta[Slide] {
  override def collectionName = "slides"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Slide(ObjectId.get, false, Nil, "", "" ,null, 
      new ObjectId("000000000000000000000000") , "" ,"Empty", 0,null ,new ObjectId(("000000000000000000000000")))
}
case class Slide(var _id: ObjectId,  var public:Boolean, var authors: List[Edit], 
				var title: String, var departmentInfo: String, var departmentId:ObjectId, 
				var referTo:ObjectId, var moderator: String, var subjectInfo: String, 
				var subjectLev:Int, var subjectId:ObjectId, var slides: ObjectId) 
				 extends MongoDocument[Slide] {
  def meta = Slide
}