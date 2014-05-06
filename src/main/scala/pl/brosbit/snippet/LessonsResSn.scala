package pl.brosbit.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed, NodeSeq}
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
import pl.brosbit.lib.DataTable
import pl.brosbit.lib.DataTableOption._


class LessonsResSn extends BaseSnippet with RoleChecker {
    
              
    val courseId = S.param("c").openOr("0")
    val course = Course.find(courseId).getOrElse(Course.findAll match {
        case c::list => c
        case _ => Course.create
    })
    val userID =  tryo(User.currentUserId.openOrThrowException("No user").toLong).openOr(0L)
              
  def dataTableScript(xhtml: NodeSeq) :NodeSeq = {
        val col = List("Id", "Tytul", "Dzial")
    DataTable("#choiceTable",
            LanguageOption("pl"),  
            ExtraOptions(Map("sPaginationType" -> "two_button")),
            SortingOption(Map( 0 -> Sorting.ASC)), 
            DisplayLengthOption(20, List(20,50, 100))
            )
  } 
    
 def renderLinkAndScript(html:NodeSeq) = DataTable.mergeSources(html)
  
 
  def teacherLessons() = {
    
    val lessons = Lesson.findAll(("authorId"->userID)~("courseId" -> course._id.toString)) 
    "tbody tr" #> lessons.map(lesson => <tr id={lesson._id.toString}>
    	<td>{lesson.nr.toString}</td><td  class="tit">
    		<a href={"/lesson/"+ lesson._id.toString} target="_blanck">{lesson.title}</a></td>
    		<td>{lesson.descript}</td>
    	<td>{if(lesson.public) "Tak" else "Nie"}</td><td
    		><a href={"/resources/editlesson/" + lesson._id.toString}>edytuj</a></td></tr>)
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
  
}
