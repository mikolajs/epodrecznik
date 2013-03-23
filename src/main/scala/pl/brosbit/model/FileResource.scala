package pl.brosbit.model 

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date


object FileResource extends MongoDocumentMeta[FileResource] {
  override def collectionName = "fileresource"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create(fileId:ObjectId, subjectId:ObjectId, departmentId:ObjectId, authorId:Long)  
  		= new FileResource(ObjectId.get,fileId, departmentId, subjectId, authorId, "","","","")
}

case class FileResource(var _id: ObjectId, var fileId:ObjectId, 
        var departmentId:ObjectId, var subjectId:ObjectId, var authorId:Long, 
        var department:String, var subject:String,var title: String, var descript:String
        ) extends  MongoDocument[FileResource] {
  def meta = FileResource
}
