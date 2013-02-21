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
  

  def slidesListPublic() = {
    val slides = Slide.findAll("public"->true)
    val edit_? = isTeacher 
    "tbody *" #> slides.map(slide => <tr><td><a href={"/slide/"+slide._id.toString}>{slide.title}</a></td>
    	<td>{slide.departmentInfo}</td><td>{slide.subjectInfo}</td><td>{slide.subjectLev.toString}</td><td>{if(edit_?)
    	<a href={"/resources/editslide/"+slide._id.toString}>edytuj</a> else <i></i>}</td></tr>)
  }
  
  //only teacher can show page (boot)
  def slidesListPrivate() =  {
    val idUser = User.currentUser.open_!.id.is 
    
    var mySlides:List[Slide] =Slide.findAll("public"->false).filter(
            slide => slide.authors.head.user == idUser.toString)
            
    "#tbodyTr" #> mySlides.map(slide =>  
      <tr><td><a href={"/slide/"+slide._id.toString}>{slide.title}</a></td>
      <td>{slide.departmentInfo}</td><td>{slide.subjectInfo}</td>
      <td>{slide.subjectLev.toString}</td>
      <td>{<a href={"/resources/editslide/"+slide._id.toString}>edytuj</a>}</td></tr>) &
    "#youCantInfo" #>  {if(idUser == 0) <h2>Musisz być zalogowany i mieć prawa edycji, aby móc dodawać tematy.
    	<a href="/contact.html">Skontaktuj się z administratorem</a></h2>
    					else <span></span> }
  }
  
   def subjectSelect() = {
    val subj = "Wszystkie"::Subject.findAll.map(s => s.full)
    "#subjectSelect *" #> subj.map(s => <option value={s}>{s}</option>)
  }
  
  
}