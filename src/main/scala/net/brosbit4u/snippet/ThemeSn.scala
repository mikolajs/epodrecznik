package net.brosbit4u.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import net.brosbit4u.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class ThemeSn {
 val id = S.param("id").openOr("0")
 var theme = if (id != "0") Theme.find(id).getOrElse(Theme.create) else Theme.create
  def canEditList() = {
    User.currentUser match {
      case Empty => "table" #> <h3>Musisz być zalogowany aby móc dodawać tematy. <a href="/contact.html">Skontaktuj się z administratorem</a></h3>
      case Full(user) => {
        val themes = if (user.level.is < 2) Theme.findAll else Theme.findAll(("author" -> user.id.toString))
        "tbody" #> themes.map(theme => {
          "tr" #> <tr><td>{theme.title}</td><td>{theme.subject}</td><td>{theme.department}</td><td><a href={ "/edit/" + theme._id.toString() }>Edytuj</a></td></tr>
        })
      }
      case Failure(msg,errorB,chain) => "table" #> <h3>Internal error {msg + " " + (if(errorB.isEmpty) "" else errorB.get.toString) } </h3>
    }
  }
  
  def slideData() = {
      "#slideList" #> <script>{Text("var slideList = " + theme.slidesListString )}</script> 
  }
  
  
  def departmentData() = {
    val depList = Department.findAll.map(d => (d.subject,d.name))
    val listStr = Subject.findAll.map(s => s.short).map(s => depList.filter(_._1 == s).map(d => "'" + d._2 + "'")).map(l =>"[" + l.mkString(",") + "]").mkString(",")
    "#departmentData" #> <script>{"var departmentData = [" + listStr + "]"}</script>
  }
  
  def formEdit() = {
    if (!canEdit) S.redirectTo("/editable")
    
    var ID = if(id == "0") "0" else theme._id.toString
    var title = theme.title
    var subject = theme.subject
    var department = theme.department
    var slidesData = "data"
    
    var i = -1;
    val listSubject = Subject.findAll.map(s => {i+= 1;(i.toString,s.full)})
    def saveData() {
      theme = if (ID != "0") Theme.find(ID).getOrElse(Theme.create) else Theme.create
      if (canEdit){
        theme.title = title
        theme.subject = subject
        theme.department = department
        if (ID == "0") theme.date = new Date().getTime().toString
        val listData = slidesData.split("000000000o0000000000o0000000000").map(_.replace('\r',' ').replace('\n',' ')) //40 ^
        theme.slides = listData.toList
       theme.save
      }
      S.redirectTo("/editable")
    }
    
    def deleteData() {
      theme = if (ID != "0") Theme.find(ID).getOrElse(Theme.create) else Theme.create
      if(isModerator) {
        theme.delete
        S.redirectTo("/editable")
      }
    }
      
    def cancelAction() {
      S.redirectTo("/editable")
    }
    
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#titleTheme" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjectTheme" #> SHtml.select(listSubject,Full(listSubject.head._2),nr => subject = listSubject(nr.toInt)._2,"onchange"->"changeDepartmentSelect();") &
    "#departmentTheme" #> SHtml.select(("pusty","pusty")::Nil,Full("noting"),department = _) &
    "#slidesData" #> SHtml.text(slidesData, slidesData = _, "type"->"hidden") &
    "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,"title"->"Zapisz","onclick"->"return createDataXML();") &
    "#delete" #> (if(isModerator) SHtml.button(<img src="/images/delico.png"/>, deleteData,"title"->"Usuń") else <span></span>) &
    "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction,"title"->"Anuluj") 
  }
  
  def subjectList() = {
    val subjects = Subject.findAll
    "#subjectList" #> subjects.map(subject => "a" #> <a href="#" onclick="selectSubject(this)">{subject.full} </a>)
  }
  
  def themeList() = {
    val themes = Theme.findAll
    
    "tbody" #> themes.map(theme => <tr><td><a href={"/slideshow/"+theme._id.toString}>{theme.title}</a></td>{theme.department}<td><td>{theme.subject}</td></td></tr>)
  }
  
  
  
  
  def canEdit:Boolean = {
    User.currentUser match {
        case Full(user) => {
          if(id == "0" || user.level.is < 2) true 
          else 
            if (theme.author == user.id.is.toString) true else false
        }
        case _ => {
          S.redirectTo("/user_mgt/login")
          false
        }
      } 
  }
  
  def isModerator = User.currentUser match {
    case Full(user) => if (user.level.is < 2) true else false
    case _ => false
  }
  
  
}
