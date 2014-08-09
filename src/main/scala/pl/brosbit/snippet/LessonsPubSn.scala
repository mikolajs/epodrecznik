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


class LessonsPubSn extends BaseSnippet with RoleChecker {
    
              
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
  
 
  //for public view in main menu
  def publicLessons() = {
      val lessons = Lesson.findAll(("public"->true)~("courseId" -> course._id.toString))
      "tbody tr" #> lessons.map(lesson => <tr id={lesson._id.toString}>
      	<td>{lesson.nr.toString}</td>
    	<td><a href={"/lesson/"+ lesson._id.toString} target="_blanck">{lesson.title}</a>
    				</td><td  class="tit">{lesson.descript}</td>
    	</tr>)
  }
  
  def courseDescription() = {
      "#coursePublic *" #> ("NIE") &
      "p *" #> course.descript
  }
  
  
  def courseSelectPublic() = {
      val courses = Course.findAll("public"->true).map(c => (c._id.toString, c.getInfo))
       "#courseSelect" #> SHtml.select(courses, Full(course._id.toString), x => x)
  }
  
}