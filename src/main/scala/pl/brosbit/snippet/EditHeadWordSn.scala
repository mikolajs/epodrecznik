package pl.brosbit.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed, Source}
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

class  EditHeadWordSn  extends BaseSlide with RoleChecker {
  
 val id = S.param("id").openOr("0")
 var headWord = if (id != "0") HeadWord.find(id).getOrElse(HeadWord.create) else HeadWord.create
 var headWordCont = HeadWordContent.find(headWord.content).getOrElse(HeadWordContent.create)
 if(headWord.content != headWordCont._id) headWord.content = headWordCont._id

 //for showheadWords - viewer 
  def headWordData() = {
      "#title" #> <span>{headWord.title}</span> &
      "#subject" #> <span>{headWord.subjectInfo}</span> 
      //"#headWordHTML" #>  Unparsed(headWordCont.sections.join("")) 
  }
  
  
  //edit headWords 
  def formEdit() = {
    
    var ID = if(id == "0") "0" else headWord._id.toString
    var title = headWord.title
    var subjectId = if(headWord.subjectId != null) headWord.subjectId.toString else ""
     var subjectLev = headWord.subjectLev.toString
    var departmentId = if(headWord.departmentId != null) headWord.departmentId.toString else ""
    var departmentInfo = headWord.departmentInfo
    var public = headWord.public
    var headWordsString = headWordCont.content
    //println("------------headWords data -----------------\n" +headWordsData)
     
    val listSubject = Subject.findAll.map(s => {(s._id.toString ,s.full)})
    
    //poprawić - uwzględnić fak, że new HeadWord już istnieje - chyba, że potrzebujemy kopi
    def saveData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.is
      if(headWord.authorId == 0L || headWord.authorId == userId) {
          val headWordsContentHtml = Unparsed(headWordsString)
          
           headWord.title = title
           headWord.subjectId = new ObjectId(subjectId)
          val sub = Subject.find(new ObjectId(subjectId)).getOrElse(Subject.create);
          headWord.subjectInfo = sub.full
          headWord.subjectLev = subjectLev.toInt
          headWord.departmentId = new ObjectId(departmentId)
          headWord.departmentInfo = Department.find(new ObjectId(departmentId)).
              getOrElse(Department.create(new ObjectId(subjectId))).name
          headWord.authorId = userId
          headWord.public = public
          headWordCont.content = headWordsString
          headWordCont.save
          headWord.content = headWordCont._id      
          headWord.save 
      }
       
      
      S.redirectTo("/resources/editheadword/"+ headWord._id.toString) //!important must refresh page
    }
    
    def deleteData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.is
     if (id != "0") HeadWord.find(id) match {
         case Some(headWord) if headWord.authorId == userId => {
           headWordCont.delete
    	   headWord.delete
        } 
         case _ =>
      }
      
      S.redirectTo("/resources/headwords")
    }
      
    def cancelAction() {
      S.redirectTo("/resources/headwords")
    }
    
   
    val publicList = List(("TAK","TAK"),("NIE","NIE"))
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#titleHeadWord" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjects" #> SHtml.select(listSubject, Full(subjectId),subjectId = _) &
    "#subjectLevel" #> SHtml.select(levList,Full(subjectLev),subjectLev = _) &
    "#public" #> SHtml.checkbox(public, public = _) &
    "#departmentHidden" #> SHtml.text(departmentId, departmentId = _, "type"->"hidden") &
    "#departments" #> departmentSelect() &
    "#headWordsData" #> SHtml.text(headWordsString, headWordsString = _, "type"->"hidden") &
    "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,"title"->"Zapisz") &
    "#delete" #> (if(isModerator) SHtml.button(<img src="/images/delico.png"/>, 
        deleteData,"title"->"Usuń") else <span></span>) &
    "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction,"title"->"Anuluj") 
  }
  
  def subjectList() = {
    val subjects = Subject.findAll
    "#subjectList" #> subjects.map(subject => "a" #> <a href="#" onclick="selectSubject(this)">{subject.full} </a>)
  } 
  
 
}
