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
import http.js.JsCmds.{SetHtml,Alert}

class EBookSn {
 val id = S.param("id").openOr("0")
 var ebook = if (id != "0") EBook.find(id).getOrElse(EBook.create) else EBook.create
  
 //ebooks => add new book and redirect to edit
  def newBook(){
   var title = ""
   var description = ""
   def save(){
     User.currentUser match {
        case Full(user) => {
          val newBook = EBook.create
          newBook.title = title
          newBook.descript = description
          newBook.owner = user.fullName
          newBook.ownerID = user.id
          newBook.save
          S.redirectTo("/book/"+newBook._id.toString)
        }
        case _ => S.redirectTo("/user_mgt/login")
      } 
   }
   "#title" #> SHtml.text(title, title = _) &
   "#description" #> SHtml.textarea(description, description = _) &
   "#save" #> SHtml.submit("Zapisz",save) 
  }
 
 //ebook => show title and description
  def ebookData() = {
      "h1" #> Text(ebook.title) &
      "p" #> <p>{ebook.descript}</p> &
      "em" #> <em>{ebook.owner}</em>  
  }
  
  //ebook => show chapter and subchapters tree and permisions and options for add chapters
  def ebookTree(){
    "ul" #> <ul></ul>
  }
  
  def chapterData(){
    "h2" #> Text("TYTUŁ")
  }
 
  /*
  //ebook => show forms for edit
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
  } */
  
  def ebookList() = {
    val books = EBook.findAll
    "tbody" #> books.map(book => "a" #> <a href="#" onclick="selectSubject(this)">{book.title} </a> &
        						 "#descriptBook *" #> <p>{book.descript}</p> & 
        						 "#ownerBook *" #> <em>{book.owner}</em> )
  }
  
  
  def canEdit:Boolean = {
    User.currentUser match {
        case Full(user) => true 
        case _ => false
      } 
  }
 
  def isOwner:Boolean = false
  
  def formTitle = {
    var title = ""
    var ID = ""
    var index = ""
    var level = "1"
    val listLevels = (1 to 4).map(x => (x.toString,x.toString) )
   "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#index" #> SHtml.text(index, index = _, "type"->"hidden") &
    "#title" #> SHtml.text(title, title= _) &
   "#level" #> SHtml.select(listLevels,Full(level),nr => level = nr) &
    "#save" #> SHtml.ajaxSubmit("Zapisz", ()=> { println("++++++++++++++++++++++++ tytuł " + title + " id " + ID + " index " + index + " level " + level)
      Alert("SAVE") }) &
    "#delete" #> SHtml.ajaxSubmit("Usuń", ()=> { println("----------------------- tytuł " + title + " id " + ID + " index " + index + " level " + level)
    	Alert("Delete")}) andThen SHtml.makeFormsAjax 
  }
  
  
 def formAddPermision(){
   "#nic" #> ""
 }

 
 def formText() = {
    var ID = ""
    var index = ""
   var content = ""
   def saveData() {
      
    }
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#index" #> SHtml.text(index, index = _, "type"->"hidden") &
    "#editor" #> SHtml.text(content, content = _) &
    "#save" #> SHtml.ajaxSubmit("Zapisz", saveData,"title"->"Zapisz","onclick"->"return createData();") andThen SHtml.makeFormsAjax
 }
  
  
  def testAjax2 = {
     var title = ""
    var ID = ""
    var index = ""
    var level = "1"
    val listLevels = (1 to 4).map(x => (x.toString,x.toString) )
    val form = "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#index" #> SHtml.text(index, index = _, "type"->"hidden") &
    "#title" #> SHtml.text(title, title= _) &
   "#level" #> SHtml.select(listLevels,Full(level),nr => level = nr) &
    "#save" #> SHtml.ajaxSubmit("Save", ()=> { println("++++++++++++++++++++++++ tytuł " + title + " id " + ID + " index " + index + " level " + level)
      Alert("SAVE") }) &
    "#delete" #> SHtml.ajaxSubmit("Delete", ()=> { println("----------------------- tytuł " + title + " id " + ID + " index " + index + " level " + level)
    	Alert("Delete")}) andThen SHtml.makeFormsAjax
    	
		   "#test2" #>  (in =>  form(in) )
  }
  
}
