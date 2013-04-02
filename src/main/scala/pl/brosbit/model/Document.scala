package pl.brosbit.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date
import org.bson.types.ObjectId

case class Chapter(var title:String, var content:String, var level:Int)
case class Person(var name:String, var email:String, var id:Long)

object Document extends MongoDocumentMeta[Document] {
  override def collectionName = "articles"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Document(ObjectId.get,"","",Nil, 0L, "", new ObjectId("000000000000000000000000"),
          "", new ObjectId("000000000000000000000000"), "")
}

case class Document(var _id: ObjectId, var title:String, var descript:String,
var chapters:List[Chapter], var ownerID:Long, var ownerName:String,
var departmentId:ObjectId, var departmentName:String, var subjectId:ObjectId,
var subcjectName:String) extends  MongoDocument[Document] {
  def meta = Document
}
