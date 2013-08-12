
package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

object Slide extends MongoDocumentMeta[Slide] {
  override def collectionName = "slides"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Slide(ObjectId.get, false, 0L, "", "", new ObjectId("000000000000000000000000"), 
      new ObjectId("000000000000000000000000") , "" , 0, new ObjectId(("000000000000000000000000")))
}
case class Slide(var _id: ObjectId,  var public:Boolean, var authorId:Long, 
				var title: String, var departmentInfo: String, var departmentId:ObjectId, 
				var subjectId:ObjectId, var subjectInfo: String, 
				var subjectLev:Int,  var slides: ObjectId) 
				 extends MongoDocument[Slide] {
  def meta = Slide
}
