package pl.brosbit.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date
import org.bson.types.ObjectId

case class Chapter(var title:String, var content:String, var level:Int)
case class Person(var name:String, var email:String, var id:Long)

object Article extends MongoDocumentMeta[Article] {
  override def collectionName = "articles"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Article(ObjectId.get,"","",Nil, Nil, 0L, "", "", "", "", "")
}

case class Article(var _id: ObjectId, var title:String, var descript:String,
var chapters:List[Chapter], var editors:List[Person], var ownerID:Long, var ownerName:String,
var departmentId:String, var departmentName:String, var subjectId:String,
var subcjectName:String) extends  MongoDocument[Article] {
  def meta = Article
}
