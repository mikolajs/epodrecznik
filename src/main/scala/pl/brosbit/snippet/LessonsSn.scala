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


class LessonsSn extends BaseSlide with RoleChecker {
    
    
  def showLesson() = {
      val lessonId = S.param("l").getOrElse("")
      val imgs = Map(("l"->"/images/link.png"), ("p"->"/images/presentation.png"), ("q"->"/images/quiz.png"),
              ("d"->"/images/document.png"))
      Lesson.find(lessonId) match {
          case Some(lesson) => "#lessonTitle *" #> lesson.title &
              "li" #>   lesson.contents.map(cont => 
              <li style={"background-image: url('" + imgs(cont.what) + "');"}>
              	<a href={cont.link}>{cont.descript}</a></li>)
          case _ => { "#lessonTitle *" #> "Nie wybrano" &
              "li" #>  <span></span>
              
          }
      }
       
  }
  

  def lessons() = {
    val lessons = Lesson.findAll("public"->true) 
    "tbody *" #> lessons.map(lesson => <tr>
    	<td><a href={"/lessons/"+lesson._id.toString}>{lesson.title}</a></td>
    	<td>{lesson.departmentInfo}</td><td>{lesson.subjectInfo}</td>
    	<td>{lesson.subjectLev.toString}</td></tr>)
  }
  
  
   def subjectSelect() = {
    val subj = "Wszystkie"::Subject.findAll.map(s => s.full)
    "#subjectSelect *" #> subj.map(s => <option value={s}>{s}</option>)
  }
  
  
}