
package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


//authors - last is older
object Quiz extends MongoDocumentMeta[Quiz] {
  override def collectionName = "quizes"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Quiz(ObjectId.get, 0L,"", "", "", null, "", 0, null, Nil)
}
case class Quiz(var _id: ObjectId,  var authorId: Long, var description: String,
				var title: String, var departmentInfo: String, var departmentId:ObjectId, 
				var subjectInfo: String, var subjectLev:Int, var subjectId:ObjectId,
				var questions: List[ObjectId])  extends MongoDocument[Quiz] {
  def meta = Quiz
}