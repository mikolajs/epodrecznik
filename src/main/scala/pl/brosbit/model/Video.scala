package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date


object Video extends MongoDocumentMeta[Video] {
  override def collectionName = "videos"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create(link:String, subjectId:ObjectId, departmentId:ObjectId, authorId:Long)  
  		= new Video(ObjectId.get, link, departmentId, subjectId, authorId, "","","","")
}

case class Video (var _id: ObjectId, var link:String, 
        var departmentId:ObjectId, var subjectId:ObjectId, var authorId:Long, 
        var department:String, var subject:String,var title: String, var descript:String
        ) extends  MongoDocument[Video] {
  def meta = Video
}
