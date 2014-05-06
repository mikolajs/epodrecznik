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
import json.JsonAST.{JObject, JArray, JValue, JBool, JField, JInt}
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class SlidesSn extends  BaseSnippet  {
  
  val user =  User.currentUser.openOrThrowException("No user")
    val subjPar = S.param("s").openOr("")
    val levPar = S.param("l").openOr("3")
    val subjectId = if  (subjPar != "") subjPar else
         Subject.findAll.head._id.toString //fail for empty subject list   

  def slidesList() = {
    val idUser =user.id.is 
    //val q1 = JObject(JField("public", JBool(true))::Nil) // for $or in one query TODO
    //val q2 = JObject(JField("authorId", JInt(idUser))::Nil)  
    val slides = Slide.findAll(("authorId"-> idUser)~("subjectLev"->levPar.toInt)~("subjectId"->subjectId))
    "tbody tr" #> slides.map(slide => {
        <tr><td><a href={"/slide/"+slide._id.toString} target="_blank">{slide.title}</a></td>
    	<td>{slide.departmentInfo}</td><td>{slide.subjectInfo}</td>
    	<td>{slide.subjectLev.toString}</td>
    	<td><a href={"/resources/editslide/"+slide._id.toString}>edytuj</a> </td></tr>
    })
  }
  
    def selectors() = {
    val subj = Subject.findAll.map(s => (s._id.toString,  s.full))
    "#subjectSelect" #>SHtml.select(subj, Full(subjPar) , x => Unit) &
    "#levelSelect" #> SHtml.select(levList, Full(levPar) , x => Unit)
    
  }
  
  
}