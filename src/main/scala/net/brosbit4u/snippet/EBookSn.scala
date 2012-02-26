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

class EBookSn {
 val id = S.param("id").openOr("0")
 var ebook = if (id != "0") EBook.find(id).getOrElse(EBook.create) else EBook.create
  
  def ebookData() = {
      "#title" #> <span>{ebook.title}</span> &
      "#descript" #> <span>{ebook.descript}</span> &
      "#owner" #> <span>{ebook.owner}</span>  
  }
  
 
  
  //edit -------------do poprawy!!!!!!!!!!!!!!!!!!!!!
  def formEdit() = {
    if (!canEdit) S.redirectTo("/user_mgt/login", S.redirectTo("/ebooks"))
    
    var ID = if(id == "0") "0" else ebook._id.toString
    var title = ebook.title
    var descript = ebook.descript
    
    
    var i = -1;
    val listSubject = Subject.findAll.map(s => {i+= 1;(i.toString,s.full)})
    def saveData() {
      if (!canEdit) S.redirectTo("/user_mgt/login")
      //if not confirmed allow write to the same ebook!
      val newEBook =  EBook.create
        newEBook.title = title
        newEBook.descript = descript
        newEBook.save 
      
      S.redirectTo("/editable")
    }
    
    def deleteData() {
     
    }
      
    def cancelAction() {
      S.redirectTo("/editable")
    }
    
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#titleEBook" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjectEBook" #> SHtml.select(listSubject,Full(descript),nr => descript = descript,"onchange"->"changeDepartmentSelect();") &
    "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,"title"->"Zapisz","onclick"->"return createData();") &
    "#delete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData,"title"->"Usuń") &
    "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction,"title"->"Anuluj") 
  }
  
  def ebookList() = {
    val books = EBook.findAll
    "#ebookList" #> books.map(book => "a" #> <a href="#" onclick="selectSubject(this)">{book.title} </a>)
  }
  
  
  def canEdit:Boolean = {
    User.currentUser match {
        case Full(user) => true 
        case _ => false
      } 
  }
 
  
  
}
