

package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

object Department extends MongoDocumentMeta[Department] {
    override def collectionName = "departments"
    override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
    def create(idSubject:ObjectId) = new Department(ObjectId.get,"",idSubject, "empty")
}

case class Department(var _id:ObjectId, var name:String, var subject:ObjectId, var subFull:String)  extends  MongoDocument[Department] {
    def meta = Department
}
