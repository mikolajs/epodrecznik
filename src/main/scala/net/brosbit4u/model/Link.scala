package net.brosbit4u.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date


object Link extends MongoDocumentMeta[Link] {
  override def collectionName = "headnews"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Link("0","","","","")
}

case class Link(var _id: String, var title: String, var imgPath:String, var content: String,var link:String) extends  MongoDocument[Link] {
  def meta = Link
}
