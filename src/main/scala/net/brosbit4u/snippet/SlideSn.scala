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

class SlideSn extends BaseSlide {
  
 val id = S.param("id").openOr("0")
 var slide = if (id != "0") Slide.find(id).getOrElse(Slide.create) else Slide.create
 var slideCont = SlideContent.find(slide.slides).getOrElse(SlideContent.create)
 if(slide.slides != slideCont._id) slide.slides = slideCont._id

 //for showslides - viewer 
  def slideData() = {
      "#title" #> <span>{slide.title}</span> &
      "#subject" #> <span>{slide.subjectInfo}</span> &
      "#etap" #> <span>{slide.subjectLev.toString}</span> &
      "#department" #> <span>{slide.departmentInfo}</span> &
      "#slideHTML" #>  Unparsed(slideCont.slides)  
  }
  
  
  def departmentSelect() = {
    val depList = Department.findAll
    val optgroups = Subject.findAll.map(s => <optgroup id={s._id.toString} > 
    	{depList.filter(d => s._id == d.subject)
    	  .map(d=> <option value={d._id.toString}>{d.name}</option>)} </optgroup>)
    <select id="departmentTheme">{optgroups}</select>
  }
  
  //edit slides 
  def formEdit() = {
    if (!canEdit) S.redirectTo("/user_mgt/login") //howto return to /editable.html???
    
    var ID = if(id == "0") "0" else slide._id.toString
    var title = slide.title
    var subjectId = if(slide.subjectId != null) slide.subjectId.toString else ""
    var subjectLev = slide.subjectLev.toString
    var departmentId = if(slide.departmentId != null) slide.departmentId.toString else ""
    var departmentInfo = slide.departmentInfo
    
    var slidesData = slideCont.slides
    //println("------------slides data -----------------\n" +slidesData)
    
    val listSubject = Subject.findAll.map(s => {(s._id.toString ,s.full)})
    
    //poprawić - uwzględnić fak, że new Slide już istnieje - chyba, że potrzebujemy kopi
    def saveData() {
      if (!canEdit) S.redirectTo("/user_mgt/login")
      //if not confirmed allow write to the same slide!
      val newSlide = if (slide.confirmed) {
        val t = Slide.create
        t.referTo = slide._id
        println("referTo = " + t.referTo.toString + " slide._id.toString = " + slide._id.toString)
        t
      } 
      else slide
        newSlide.title = title
        newSlide.subjectId = new ObjectId(subjectId)
        val sub =  Subject.find(new ObjectId(subjectId)).getOrElse(Subject.create);
        newSlide.subjectInfo = sub.full
        newSlide.subjectLev = subjectLev.toInt
        newSlide.departmentId = new ObjectId(departmentId)
        newSlide.departmentInfo = Department.find(new ObjectId(departmentId)).
        	getOrElse(Department.create(new ObjectId(subjectId))).name
        	
        val userID = User.currentUser.get.id.is.toString
   
        newSlide.authors = if (newSlide.authors.exists(e => e.user == userID)) newSlide.authors.map(e => {
        										if (e.user == userID) Edit(userID, new Date().getTime().toString)
        										else e
        										}) 
        else Edit(userID,new Date().getTime().toString)::newSlide.authors
        newSlide.confirmed = false
        val listData = Unparsed(slidesData)
        newSlide.slides = listData.toString
        newSlide.save 
      
      S.redirectTo("/editable")
    }
    
    def deleteData() {
      slide = if (ID != "0") Slide.find(ID).getOrElse(Slide.create) else Slide.create
      if (slide.confirmed) {
    	  if(isModerator) {
        //może kiedyś dodać zamiast usuwania zapis do backupu? albo tylko admin mógłby usuwać?
    		  slide.delete
    		  S.redirectTo("/editable")
    	  }
      } 
      else {
        if(canEdit){
           val idAuthor = slide.authors.head.user.toInt
           User.currentUser match {
             case Full(user) => if(idAuthor == user.id.is) slide.delReq = true
             case _ => 
           }
           S.redirectTo("/editable")
          
        }
      }
    }
      
    def cancelAction() {
      S.redirectTo("/editable")
    }
    val levList = List(("1","I"),("2","II"),("3","III"),("4","IV"))
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#titleTheme" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjectTheme" #> SHtml.select(listSubject, Full(subjectId),subjectId = _) &
    "#subjectLevel" #> SHtml.select(levList,Full(subjectLev),subjectLev = _) &
    "#departmentThemeHidden" #> SHtml.text(departmentId, departmentId = _, "type"->"hidden") &
    "#departmentTheme" #> departmentSelect() &
    "#slidesData" #> SHtml.text(slidesData, slidesData = _, "type"->"hidden") &
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