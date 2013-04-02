package pl.brosbit.snippet

import java.util.Date
import scala.xml.{ Text, XML, Unparsed, Elem, Null, TopScope }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

//for show list of all doc and create new doc
class DocumentsSn extends BaseSlide {
//edocuments => show edocuments list 
 def edocList() = {
    val documents = Document.findAll   //all can view??
    "tbody" #> documents.map(doc => "a" #> <a href={"/resources/showdocument/" + doc._id.toString} >{ doc.title } </a> &
      ".descriptBook *" #> <p>{ doc.descript }</p> &
      ".ownerBook *" #> <em>{ doc.ownerName }</em> &
      ".subject *" #> Text(doc.subcjectName) &
      ".department *" #> Text(doc.departmentName) &
      ".editBook *" #> <a href={"/resources/editdocument/" + doc._id.toString} target="_blank"> Edytuj</a>
      )
  }

}