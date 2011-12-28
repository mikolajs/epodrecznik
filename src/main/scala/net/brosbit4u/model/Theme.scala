
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date

case class Slajd(var nr:Int, var title:String, var img:String, var text:String, var layout:Char)

object Theme extends MongoDocumentMeta[Theme] {
    override def collectionName = "subjectSlajds"
    override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
    def create = new Theme(0L,"","","","","",Nil)
}

case class Theme(var _id:Long, var author:String, var title:String, var department:String, var date:String, var subject:String, var slajds:List[Slajd] )
 extends  MongoDocument[Theme] {
     def meta = Theme
 }