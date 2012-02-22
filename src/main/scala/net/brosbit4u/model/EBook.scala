package net.brosbit4u.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date
import org.bson.types.ObjectId

case class Chapter(title:String, level:Int, content:String, comments:String, permision:List[String])

object EBook extends MongoDocumentMeta[EBook] {
  override def collectionName = "ebook"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new EBook(ObjectId.get,"","",Nil,"")
}

case class EBook(var _id: ObjectId, title:String, var descript:String, var chapter:List[Chapter], owner:String) extends  MongoDocument[EBook] {
  def meta = EBook
}
