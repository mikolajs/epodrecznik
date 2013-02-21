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
class EBookMkSn {
	//ebooks => add new book and redirect to edit
  def newBook() = {
    var title = ""
    var description = ""
    def save() {
      User.currentUser match {
        case Full(user) => {
          val newBook = EBook.create
          newBook.title = title
          newBook.descript = description
          newBook.owner = user.fullName
          newBook.ownerID = user.id
          newBook.save
          S.redirectTo("/ebook/" + newBook._id.toString + "/1")
        }
        case _ => S.redirectTo("/user_mgt/login")
      }
    }
    "#title" #> SHtml.text(title, title = _) &
      "#description" #> SHtml.textarea(description, description = _) &
      "#save" #> SHtml.submit("Zapisz", save)
  }
//ebooks => show ebooks list 
 def ebookList() = {
    val books = EBook.findAll
    "tbody" #> books.map(book => "a" #> <a href={"/ebook/" + book._id.toString + "/1"} >{ book.title } </a> &
      "#descriptBook *" #> <p>{ book.descript }</p> &
      "#ownerBook *" #> <em>{ book.owner }</em>)
  }

}