package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

object Dictionary extends MongoDocumentMeta[Dictionary] {
	override def collectionName = "dictionary"
    override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
    def create() = new Dictionary(ObjectId.get, ObjectId.get, "", "empty", Nil, "")
}

case class Dictionary(var _id:ObjectId, var subject:ObjectId, var entry:String, var content:String,
        				var synonyms:List[String],  var subjectName:String)
        				extends  MongoDocument[Dictionary] {
    def meta = Dictionary
}
