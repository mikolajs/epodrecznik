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
import http.js.JsCmds.SetHtml
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class LessonsSn extends BaseSlide with RoleChecker {
    
    val imgs = Map(("l"->"/images/link.png"), ("p"->"/images/presentation.png"), ("q"->"/images/quiz.png"),
              ("d"->"/images/document.png"),("a"->"/images/attachment.png"),("v"->"/images/video.png"))
              
  def showLesson() = {
      var lessonId = ""
      def create(id:String) = {
          Lesson.find(id) match {
              case Some(lesson) => {
                  <div>
            	  <div id="lessonHead"><span id="theme"><strong>Temat: </strong>{lesson.title}</span><br/>
            	  <span id="public"><strong>Widoczność: </strong>{if(lesson.public) "Publiczny" else "Prywatny"}</span><br/>
            	  <span id="subject"><strong>Przedmiot: </strong> {lesson.subjectInfo}</span><br/>
            	  <span id="department"><strong>Dział: </strong>{lesson.departmentInfo}</span><br/>
            	  <span id="level"><strong>Poziom: </strong>{lesson.lev.toString}</span></div>
            	  <div id="lessonsContent">
            	  <ul id="lessonList">
            	  {lesson.contents.map(cont => {
            	     <li><a href={cont.link} target="_blank"><img src={imgs(cont.what)} />
            	     <span class="title">{cont.title}</span></a>
            	  <p class="desc">{cont.descript}</p>
            	  </li>  
            	  })}
            	  </ul>
            	  </div>
            	  <a href={"/resources/editlesson/"+ lesson._id.toString}>EDYTUJ</a>
            	  </div>
              }
              case _ => <div>Błąd - brak wybranej lekcji</div>
          }   
      }     
      "#hiddenAjaxText" #> SHtml.ajaxText("",  id => SetHtml("ajaxLesson", create(id)))          
  }
  
  
  def showLessonPublic() = {
      var lessonId = ""
      
      def create(id:String) = {
          Lesson.find(id) match {
              case Some(lesson) => {
                  <div>
            	  <div id="lessonHead"><span id="theme"><strong>Temat: </strong>{lesson.title}</span><br/>
            	  <span id="subject"><strong>Przedmiot: </strong> {lesson.subjectInfo}</span><br/>
            	  <span id="department"><strong>Dział: </strong>{lesson.departmentInfo}</span><br/>
            	  <span id="level"><strong>Poziom: </strong>{lesson.lev.toString}</span></div>
            	  <div id="lessonsContent">
            	  <ul id="lessonList">
            	  {lesson.contents.map(cont => {
            	     <li><a href={cont.link} target="_blank"><img src={imgs(cont.what)} />
            	     <span class="title">{cont.title}</span></a>
            	  <p class="desc">{cont.descript}</p>
            	  </li>  
            	  })}
            	  </ul>
            	  </div>
            	  </div>
              }
              case _ => <div>Błąd - brak wybranej lekcji</div>
          }      
      }          
      "#hiddenAjaxText" #> SHtml.ajaxText("",  id => SetHtml("ajaxLesson", create(id)))       
  }
  

  def teacherLessons() = {
    val userID =  tryo(User.currentUserId.open_!.toLong).openOr(0L)
    val lessons = Lesson.findAll("authorId"->userID) 
    "tbody *" #> lessons.map(lesson => <tr>
    	<td id={lesson._id.toString} class="tit">{lesson.title}</td>
    	<td>{lesson.departmentInfo}</td><td>{lesson.subjectInfo}</td>
    	<td>{lesson.lev.toString}</td></tr>)
  }
  
  //for public view in main menu
  def publicLessons() = {
      val lessons = Lesson.findAll("public"->true)
      "tbody *" #> lessons.map(lesson => <tr>
    	<td id={lesson._id.toString} class="tit">{lesson.title}</td>
    	<td>{lesson.departmentInfo}</td><td>{lesson.subjectInfo}</td>
    	<td>{lesson.lev.toString}</td></tr>)
  }
  
  
   def subjectSelect() = {
    val subj = "Wszystkie"::Subject.findAll.map(s => s.full)
    "#subjectSelect *" #> subj.map(s => <option value={s}>{s}</option>)
  }
  
  
}