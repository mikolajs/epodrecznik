package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId



//authors - last is older
object QuizQuestion extends MongoDocumentMeta[QuizQuestion] {
  override def collectionName = "questions"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new QuizQuestion(ObjectId.get, 0L, 0, 0, false, null, null, "", "", Nil)
}
case class QuizQuestion(var _id: ObjectId,  var authorId: Long,  var dificult:Int, var level:Int,
				var public:Boolean, var departmentId:ObjectId, var subjectId:ObjectId,
				var question:String, var answer:String,
				var fake:List[String])  extends MongoDocument[QuizQuestion] {
  def meta = QuizQuestion
}