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
              
    val courseId = S.param("c").openOr("0")
    val course = Course.find(courseId).getOrElse(Course.findAll match {
        case c::list => c
        case _ => Course.create
    })
    val userID =  tryo(User.currentUserId.openOrThrowException("No user").toLong).openOr(0L)
              
  def showLesson() = {
      var lessonId = ""
      def create(id:String) = {
          Lesson.find(id) match {
              case Some(lesson) => {
                  val courseInfo = course.getInfo
                  <div>
            	  <div id="lessonHead">
                  <span id="lp"><strong>LP: </strong> {lesson.nr.toString}</span>
                  <span id="theme"><strong>Temat: </strong>{lesson.title}</span><br/>
            	  <span id="public"><strong>Widoczność: </strong>
            	   	{if(lesson.public) "Publiczny" else "Prywatny"}</span><br/>
            	  <span id="course"><strong>Kurs: </strong>{courseInfo}</span></div>
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
                  val courseInfo = course.getInfo
                  <div>
            	  <div id="lessonHead">
                		  <span id="lp"><strong>LP: </strong> {lesson.nr.toString}</span>
                		  <span id="theme"><strong>Temat: </strong>{lesson.title}</span><br/>
            	   <span id="course"><strong>Kurs: </strong>{courseInfo}</span></div>
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
    
    val lessons = Lesson.findAll(("authorId"->userID)~("courseId" -> course._id.toString)) 
    "tbody tr" #> lessons.map(lesson => <tr id={lesson._id.toString}>
    	<td>{lesson.nr.toString}</td><td  class="tit">{lesson.title}</td>
    	<td>{if(lesson.public) "Tak" else "Nie"}</td></tr>)
  }
  
  //for public view in main menu
  def publicLessons() = {
      val lessons = Lesson.findAll(("public"->true)~("courseId" -> course._id.toString))
      "tbody tr" #> lessons.map(lesson => <tr id={lesson._id.toString}>
    	<td>{lesson.nr.toString}</td><td  class="tit">{lesson.title}</td>
    	</tr>)
  }
  
  def courseDescription() = {
      "#coursePublic *" #> (if(course.public) "TAK" else "NIE") &
      "p *" #> course.descript
  }
  
  def courseSelectTeacher() = {
      
       val courses = Course.findAll("authorId"-> userID).map(c => (c._id.toString, c.getInfo))
      "#courseSelect" #> SHtml.select(courses, Full(course._id.toString), x => x)
       //courses.map(c => <option value={c._1} > {c._2} </option> )
  }
  
  def courseSelectPublic() = {
      val courses = Course.findAll("public"->true).map(c => (c._id.toString, c.getInfo))
       "#courseSelect" #> SHtml.select(courses, Full(course._id.toString), x => x)
  }
  
}