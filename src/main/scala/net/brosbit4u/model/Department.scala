

package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date

object Department extends MongoDocumentMeta[Department] {
    override def collectionName = "departments"
    override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
    def create = new Department(0L,"","","")
}

case class Department(var _id:Long, var short:String, var name:String, var subject:String)  extends  MongoDocument[Department] {
    def meta = Department
}
