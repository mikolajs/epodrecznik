package pl.brosbit.snippet

import pl.brosbit.model._
import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import mapper.By
import json.JsonDSL._
import json.JsonAST.{JObject, JArray, JValue, JBool, JField, JInt}
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class HeadWordsSn extends  RoleChecker {
  
    val idUser = User.currentUser.openOrThrowException("No user").id.is 
    val subjPar = S.param("s").openOr("")
    val levPar = S.param("l").openOr("3")
    val subiectId = Subject.find(subjPar) match {
        case Some(sub) => subjPar
        case _ => Subject.findAll.head._id.toString
    }
    
  def headWordsList() = {
   
    //val q1 = JObject(JField("public", JBool(true))::Nil) // for $or in one query TODO
    //val q2 = JObject(JField("authorId", JInt(idUser))::Nil) 
    val headWords1 = HeadWord.findAll(("public"->true)~("subjectLev"->levPar.toInt)~("subjectId"->subjPar))
    val headWords2 = HeadWord.findAll(("authorId"-> idUser)~("public"->false)~("subjectLev"->levPar.toInt)~("subjectId"->subjPar))
    val headWords =headWords1 :::headWords2
    "tbody tr" #>headWords.map(headWord => {
        val edit_? = headWord.authorId == idUser
        <tr><td><a href={"/headword/"+headWord._id.toString} target="_blank">{headWord.title}</a></td>
    	<td>{if(edit_?) { if(headWord.public) "Publiczny" else "Prywatny"} else "Udostępniony"}</td>
    	<td>{if(edit_?)
    	<a href={"/resources/editheadword/"+headWord._id.toString}>edytuj</a> else <i></i>}</td></tr>
    })
  }
  
  def selectors() = {
    val subj = Subject.findAll.map(s => (s._id.toString,  s.full))
     val levList = List(("1","szkoła podstawowa"),("2","gimanzjum - podstawa LO"),("3","rozszerzenie - LO"),("4","studia"))
    "#subjectSelect" #>SHtml.select(subj, Full(subjPar) , x => Unit) &
    "#levelSelect" #> SHtml.select(levList, Full(levPar) , x => Unit)
    
  }
  
}
