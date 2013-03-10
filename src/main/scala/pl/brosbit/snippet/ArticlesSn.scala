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

//for show list of all book and create new book
class ArticlesSn {
//ebooks => show ebooks list 
 def ebookList() = {
    val books = Article.findAll
    "tbody" #> books.map(book => "a" #> <a href={"/ebook/" + book._id.toString + "/1"} >{ book.title } </a> &
      "#descriptBook *" #> <p>{ book.descript }</p> &
      "#ownerBook *" #> <em>{ book.ownerName }</em>)
  }

}