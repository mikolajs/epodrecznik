package net.brosbit4u.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date
import org.bson.types.ObjectId

case class SubChapter(var title:String, var content:String, var level:Int)
case class Person(var email:String, var id:Long)
case class Chapter( var subchapters:List[SubChapter], var permission:List[Person])

object EBook extends MongoDocumentMeta[EBook] {
  override def collectionName = "ebook"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new EBook(ObjectId.get,"","",Nil,"",0L)
}

case class EBook(var _id: ObjectId, var title:String, var descript:String, var chapters:List[Chapter], var owner:String, var ownerID:Long) extends  MongoDocument[EBook] {
  def meta = EBook
}
