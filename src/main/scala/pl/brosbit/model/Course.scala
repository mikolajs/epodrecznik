package pl.brosbit.model 

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date


object Course extends MongoDocumentMeta[Course] {
  override def collectionName = "courses"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Course(ObjectId.get, false, "", ObjectId.get, "", "", 0, 0L)
}

case class Course(var _id: ObjectId,  var public:Boolean, var title: String, var subjectId:ObjectId, 
        			var subjectName: String,  var descript: String, var level:Int,
        			var authorId: Long) extends  MongoDocument[Course] {
  def meta = Course
  def getInfo = title + " - " + subjectName + ", poziom " + level
}
