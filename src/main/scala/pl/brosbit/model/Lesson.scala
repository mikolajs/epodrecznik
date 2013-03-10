package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class LessonContent(what:String, link:String, title:String, descript:String) {
    def forJSONStr = "{\"what\":\"" + what + "\",\"link\":\"" + link + "\",\"title\":\"" + 
    						title + "\",\"descript\":\"" + descript + "\"}"
}

object Lesson extends MongoDocumentMeta[Lesson] {
  override def collectionName = "lessons"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Lesson(ObjectId.get, false, "author", 0L,  "", "" ,
          		new ObjectId("000000000000000000000000"), "", "", 0, 
          		new ObjectId("000000000000000000000000"), 0, Nil)
}

case class Lesson(var _id: ObjectId,  var public:Boolean, var authorName: String, 
        		var authorId:Long, var title: String, var departmentInfo: String, var departmentId:ObjectId, 
				 var moderator: String, var subjectInfo: String, var subjectLev:Int, 
				 var subjectId:ObjectId, var lev: Int, var contents: List[LessonContent]) 
				 extends MongoDocument[Lesson] {
  def meta = Lesson
}