package pl.brosbit.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class SlidesSn extends BaseSlide with RoleChecker {
  

  def slidesList() = {
    val idUser = User.currentUser.open_!.id.is 
    val slides = Slide.findAll("$or"->("public"->true)~("authorId"->idUser)) 
    "tbody *" #> slides.map(slide => {
        val edit_? = slide.authorId == idUser
        <tr><td><a href={"/slide/"+slide._id.toString}>{slide.title}</a></td>
    	<td>{slide.departmentInfo}</td><td>{slide.subjectInfo}</td>
    	<td>{slide.subjectLev.toString}</td>
    	<td>{if(edit_?) { if(slide.public) "Publiczny" else "Prywatny"} else "Udostępniony"}</td>
    	<td>{if(edit_?)
    	<a href={"/resources/editslide/"+slide._id.toString}>edytuj</a> else <i></i>}</td></tr>
    })
  }
  
}