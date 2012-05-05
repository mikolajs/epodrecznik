package net.brosbit4u.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date
import org.bson.types.ObjectId

case class AddedContent(info:String, what:String, link:String, date:String)

object NewContent extends MongoDocumentMeta[NewContent] {
  override def collectionName = "newcontent"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new NewContent(ObjectId.get,Nil)
}

case class NewContent(var _id: ObjectId, var content:List[AddedContent]) extends  MongoDocument[NewContent] {
  def meta = NewContent
}
