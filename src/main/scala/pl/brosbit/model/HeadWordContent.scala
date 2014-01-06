package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

//authors - last is older
object HeadWordContent extends MongoDocumentMeta[HeadWordContent] {
  override def collectionName = "headWordsContent"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new HeadWordContent(ObjectId.get, "")
}

case class HeadWordContent(var _id: ObjectId,  var content:String 
        				) extends MongoDocument[HeadWordContent] {
  def meta = HeadWordContent
}
