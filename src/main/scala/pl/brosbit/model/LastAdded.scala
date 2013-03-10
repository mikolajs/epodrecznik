package pl.brosbit.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date
import org.bson.types.ObjectId

case class LastAddedItem(info:String, subject:String, link:String, date:String)

object LastAdded extends MongoDocumentMeta[LastAdded] {
  override def collectionName = "lastadded"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new LastAdded(ObjectId.get,Nil)
}

case class LastAdded(var _id: ObjectId, var content:List[LastAddedItem]) extends  MongoDocument[LastAdded] {
  def meta = LastAdded
}
