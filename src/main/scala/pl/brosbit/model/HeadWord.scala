package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

object HeadWord extends MongoDocumentMeta[HeadWord] {
  override def collectionName = "HeadWords"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new HeadWord(ObjectId.get, false, 0L, "", new ObjectId("000000000000000000000000") , 
          			"" , new ObjectId("000000000000000000000000"), "",  0, "")
}
case class HeadWord(var _id: ObjectId,  var public:Boolean, var authorId:Long, 
				var title: String,   var  subjectId:ObjectId, var subjectInfo: String,   var departmentId:ObjectId, 
				var departmentInfo:String,  var subjectLev:Int, var content: String) 
				 extends MongoDocument[HeadWord] {
  def meta = HeadWord
}