package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

object Dictionary extends MongoDocumentMeta[Dictionary] {
	override def collectionName = "dictionary"
    override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
    def create() = new Dictionary(ObjectId.get, ObjectId.get, "", "", 0, true, 0L, "", "")
}

case class Dictionary(var _id:ObjectId, var subject:ObjectId, var headword:String, var content:String,
						var level:Int, var draft:Boolean, var authorId:Long, var authorName:String,
						var subjectName:String) extends  MongoDocument[Dictionary] {
    def meta = Dictionary
}
