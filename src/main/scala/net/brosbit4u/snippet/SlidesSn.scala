package net.brosbit4u.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import _root_.net.brosbit4u.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class SlidesSn extends BaseSlide {
  
 //show all themes to show
  def themeListConfirmed() = {
    val themes = Slide.findAll("confirmed"->true)
    val edit_? = canEdit 
    "tbody *" #> themes.map(slide => <tr><td><a href={"/slideshow/"+slide._id.toString}>{slide.title}</a></td>
    	<td>{slide.departmentInfo}</td><td>{slide.subjectInfo}</td><td>{slide.subjectLev.toString}</td><td>{if(edit_?)
    	<a href={"/edit/"+slide._id.toString}>edytuj</a> else <i></i>}</td></tr>)
  }
  
  def themeListPrivate() =  {
    val idUser = User.currentUser match {
      
       case Full(user) => {
          if(user.level.is < 8) user.id.is
          else 0
        }
        case _ => 0
    }
    var myThemes:List[Slide] = Nil 
    if (idUser > 0) {
      myThemes = Slide.findAll("confirmed"->false).filter(them => them.authors.head.user == idUser.toString)
    }
    "#tbodyTr" #> myThemes.map(them =>  <tr><td><a href={"/slideshow/"+them._id.toString}>{them.title}</a></td>
    	<td>{them.departmentInfo}</td><td>{them.subjectInfo}</td><td>{them.subjectLev.toString}</td>
<td>{<a href={"/edit/"+them._id.toString}>edytuj</a>}</td></tr>) &
    "#youCantInfo" #>  {if(idUser == 0) <h2>Musisz być zalogowany i mieć prawa edycji, aby móc dodawać tematy.
 <a href="/contact.html">Skontaktuj się z administratorem</a></h2>
    					else <span></span> }
    
  }
  
   def subjectSelect() = {
    val subj = "Wszystkie"::Subject.findAll.map(s => s.full)
    "#subjectSelect *" #> subj.map(s => <option value={s}>{s}</option>)
  }
  
  
}