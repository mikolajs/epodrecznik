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
  
  def slideData() = {
      "#title" #> <span>{theme.title}</span> &
      "#subject" #> <span>{theme.subject}</span> &
      "#department" #> <span>{theme.department}</span> &
      "#slideHTML" #>  Unparsed(theme.slides)  
  }
  
  
  def departmentData() = {
    val depList = Department.findAll.map(d => (d.subject,d.name))
    val listStr = Subject.findAll.map(s => s.full).map(s => depList.filter(_._1 == s).map(d => "'" + d._2 + "'")).map(l =>"[" + l.mkString(",") + "]").mkString(",")
    "#departmentData" #> <script>{"var departmentData = [" + listStr + "]"}</script>
  }
  
  //edit slides 
  def formEdit() = {
    if (!canEdit) S.redirectTo("/user_mgt/login", S.redirectTo("/editable"))
    
    var ID = if(id == "0") "0" else theme._id.toString
    var title = theme.title
    var subject = theme.subject
    var department = theme.department
    var slidesData = ""
    
    var i = -1;
    val listSubject = Subject.findAll.map(s => {i+= 1;(i.toString,s.full)})
    def saveData() {
      if (!canEdit) S.redirectTo("/user_mgt/login")
      //if not confirmed allow write to the same theme!
      val newTheme = if (theme.confirmed) Theme.create else theme
        newTheme.title = title
        newTheme.subject = subject
        newTheme.department = department
        val edit = Edit(User.currentUser.get.id.is.toString,new Date().getTime().toString)
        newTheme.edit = edit::theme.edit
        newTheme.confirmed = false
        val listData = Unparsed(slidesData)
        newTheme.slides = listData.toString
        newTheme.referTo = if (ID == "0") "0" else theme._id.toByteArray().mkString("")
        newTheme.save 
      
      S.redirectTo("/editable")
    }
    
    def deleteData() {
      theme = if (ID != "0") Theme.find(ID).getOrElse(Theme.create) else Theme.create
      if(isModerator) {
        //może kiedyś dodać zamiast usuwania zapis do backupu?
        theme.delete
        S.redirectTo("/editable")
      }
    }
      
    def cancelAction() {
      S.redirectTo("/editable")
    }
    
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#titleTheme" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjectTheme" #> SHtml.select(listSubject,Full(subject),nr => subject = listSubject(nr.toInt)._2,"onchange"->"changeDepartmentSelect();") &
    "#departmentThemeHidden" #> SHtml.text(department, department = _, "type"->"hidden") &
    "#departmentTheme" #> SHtml.select(("pusty","pusty")::Nil,Full("noting"),x => Unit) &
    "#slidesData" #> SHtml.text(slidesData, slidesData = _, "type"->"hidden") &
    "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,"title"->"Zapisz","onclick"->"return createData();") &
    "#delete" #> (if(isModerator) SHtml.button(<img src="/images/delico.png"/>, deleteData,"title"->"Usuń") else <span></span>) &
    "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction,"title"->"Anuluj") 
  }
  
  def subjectList() = {
    val subjects = Subject.findAll
    "#subjectList" #> subjects.map(subject => "a" #> <a href="#" onclick="selectSubject(this)">{subject.full} </a>)
  }
  
  
  //show all themes to show
  def themeListConfirmed() = {
    val themes = Theme.findAll("confirmed"->true)
    val edit_? = canEdit 
    "tbody" #> themes.map(theme => <tr><td><a href={"/slideshow/"+theme._id.toString}>{theme.title}</a></td>
    	<td>{theme.department}</td><td>{theme.subject}</td><td>{if(edit_?) <a href={"/edit/"+theme._id.toString}>edytuj</a> else <i></i>}</td></tr>)
  }
  
  def themeListPrivate() =  {
    val idUser = User.currentUser match {
      
       case Full(user) => {
          if(user.level.is < 8) user.id.is
          else 0
        }
        case _ => 0
    }
    var myThemes:List[Theme] = Nil 
    if (idUser > 0) {
      myThemes = Theme.findAll("confirmed"->false).filter(them => them.edit.head.user == idUser.toString)
    }
    "tbody" #> myThemes.map(them =>  <tr><td><a href={"/slideshow/"+them._id.toString}>{them.title}</a></td>
    	<td>{them.department}</td><td>{them.subject}</td><td>{<a href={"/edit/"+them._id.toString}>edytuj</a>}</td></tr>) &
    "#youCantInfo" #>  {if(idUser == 0) <h2>Musisz być zalogowany i mieć prawa edycji, aby móc dodawać tematy. <a href="/contact.html">Skontaktuj się z administratorem</a></h2>
    					else <span></span> }
    
  }
  
  
  def canEdit:Boolean = {
    User.currentUser match {
        case Full(user) => {
          if(user.level.is < 7) true 
          else false
        }
        case _ => {
          false
        }
      } 
  }
  
  def isModerator = User.currentUser match {
    case Full(user) => if (user.level.is < 2) true else false
    case _ => false
  }
  
  
}
