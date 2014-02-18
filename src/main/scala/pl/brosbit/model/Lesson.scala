package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class LessonContent(what:String, link:String, title:String, depart:String) {
    def forJSONStr = "{\"what\":\"" + what + "\",\"link\":\"" + link + "\",\"title\":\"" + 
    						title + "\",\"depart\":\"" + depart + "\"}"
}
// what: headword - h,  quest - q

case class LessonItem(what:String, id:String, title:String)

object Lesson extends MongoDocumentMeta[Lesson] {
  override def collectionName = "lessons"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Lesson(ObjectId.get, false, 0, 0L,  "",  "", "",
          		new ObjectId("000000000000000000000000"),  Nil)
}

case class Lesson(var _id: ObjectId,  var public:Boolean, var nr: Int,
        		var authorId:Long, var title: String,  var extraText:String, var descript: String,
				 var courseId:ObjectId, var contents: List[LessonContent]) 
				 extends MongoDocument[Lesson] {
  def meta = Lesson
}