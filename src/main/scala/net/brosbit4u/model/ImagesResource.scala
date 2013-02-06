
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

object ImagesResource extends MongoDocumentMeta[ImagesResource] {
    override def collectionName = "departments"
    override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
    def create(fileId:ObjectId, subjectId:ObjectId, departmentId:ObjectId, authorId:Long) 
    	= new ImagesResource(ObjectId.get, fileId, subjectId, departmentId ,authorId , "")
}

case class ImagesResource(var _id:ObjectId, var fileId:ObjectId, var subjectId:ObjectId, 
        	var department:ObjectId, authorId:Long, authorName:String)  extends  MongoDocument[ImagesResource] {
    def meta = ImagesResource
}
