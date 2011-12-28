

package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date

object Subject extends MongoDocumentMeta[Subject] {
    override def collectionName = "subjects"
    override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
    def create = new Subject(0L,"","")
}

case class Subject(var _id:Long, var short:String, var full:String)  extends  MongoDocument[Subject] {
    def meta = Subject
}
