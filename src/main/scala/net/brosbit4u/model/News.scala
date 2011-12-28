package net.brosbit4u.model 

import _root_.net.liftweb.mongodb._
//import org.bson.types.ObjectId
import java.util.Date


object News extends MongoDocumentMeta[News] {
  override def collectionName = "headnews"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new News("0","","","")
}

case class News(var _id: String,var title: String, var content: String,var date:String) extends  MongoDocument[News] {
  def meta = News
}
