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

//przerobić 
class EditDictionarySn extends BaseSlide with RoleChecker {
   val id = S.param("id").openOr("0")
 var dictEntry = if (id != "0") Dictionary.find(id).getOrElse(Dictionary.create) else Dictionary.create


 //for showslides - viewer 
  def slideData() = {
      "#title" #> <span>{dictEntry.entry}</span> &
      "#subject" #> <span>{dictEntry.subjectName}</span> &
      "#slideHTML" #>  Unparsed(dictEntry.content)  
  }
  
  
  //edit slides 
  def formEdit() = {
    
    var ID = if(id == "0") "0" else dictEntry._id.toString
    var headword = dictEntry.entry
    var subjectId = if(dictEntry.subject != null) dictEntry.subject.toString else ""
    var subjectName = dictEntry.subjectName
    
    var data = dictEntry.content

    val listSubject = Subject.findAll.map(s => {(s._id.toString ,s.full)})
    
 
    def saveData() {
      
      val contentHtml = Unparsed(data)    
    
      dictEntry.entry = headword
      dictEntry.subject = new ObjectId(subjectId)
      val sub = Subject.find(new ObjectId(subjectId)).getOrElse(Subject.create);
      dictEntry.subjectName = sub.full
  
      val userID = User.currentUser.get.id.is.toString
   
   
      dictEntry.content = contentHtml.toString    
      dictEntry.save
    
      S.redirectTo(makeURL) //!important must refresh page
    }
    
    def deleteData() {
      dictEntry = if (ID != "0") Dictionary.find(ID).getOrElse(Dictionary.create) else Dictionary.create
      dictEntry.delete
      S.redirectTo("/dictionary")
    }
      
    def cancelAction() {
      S.redirectTo(makeURL)
    }
    
    //val levList = List(("1","I"),("2","II"),("3","III"),("4","IV"))
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#entry" #> SHtml.text(headword, x => headword = x.trim, "class"->"Name") &
    "#subjects" #> SHtml.select(listSubject, Full(subjectId),subjectId = _) &
    "#contentData" #> SHtml.text(data, data = _, "type"->"hidden") &
    "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,"title"->"Zapisz") &
    "#delete" #> (if(isModerator) SHtml.button(<img src="/images/delico.png"/>, 
        deleteData,"title"->"Usuń") else <span></span>) &
    "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction,"title"->"Anuluj") 
  }
  
  def subjectList() = {
    val subjects = Subject.findAll
    "#subjectList" #> subjects.map(subject => "a" #> <a href="#" onclick="selectSubject(this)">{subject.full} </a>)
  } 
  
  private def makeURL = "/dictionary?s="+dictEntry._id.toString + "&e=" + dictEntry.entry
    
}