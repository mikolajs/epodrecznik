package pl.brosbit.snippet


import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class CoursesSn extends BaseSlide {
	def showMyCourses() = {
	    val user = User.currentUser.open_!
	    
	     "tr" #> Course.findAll("authorId" -> user.id.is).map(course => {
	        <tr id={course._id.toString}><td>{course.title}</td>
	        <td>{course.subjectName}</td><td>{course.level}</td>
	        <td>{if(course.public) "TAK" else "NIE"}</td>
	        <td>{course.descript}</td>
	        </tr>
	    })
	}

    def add() = {
        

        var id = ""
        var level = ""
        var title = ""
        var descript = ""
        var subjectId = ""
        var public = false

        def save() {
            val user = User.currentUser.open_!

            val sub = Subject.find(subjectId).getOrElse(Subject.create)

            val course = Course.find(id).getOrElse(Course.create)
            
            course.level = tryo(level.toInt).openOr(1)
            course.title = title
            course.subjectId = if(sub.full != "") sub._id else Subject.findAll.head._id
            course.subjectName = sub.full
            course.descript = descript
            course.authorId = user.id
            course.public = public
            course.save
        }
        
        def delete() {
            Course.find(id) match {
                case Some(course) => {
                    val lessonsInCourse = Lesson.findAll("courseId" -> course._id.toString)
                    if(lessonsInCourse.length == 0) {
                        course.delete
                    }
                }
                case _ =>   
            }
        }

        val subjects = Subject.findAll.map(sub => (sub._id.toString, sub.full))
        val levels = List(("1", "I"), ("2", "II"), ("3", "III"), ("4", "IV"), ("5", "V"))
	   
	    "#courseId"  #> SHtml.text(id, id = _) &
	    "#title" #> SHtml.text(title, x => title = x.trim) &
	    "#subjects" #> SHtml.select(subjects, Full(subjects.head._1), subjectId = _) &
	    "#level" #> SHtml.select(levels, Full(levels.head._1), level = _) &
	    "#public" #> SHtml.checkbox(public, public = _) &
	    "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
	    "#save" #> SHtml.submit("Dodaj", save) & 
	    "#delete" #> SHtml.submit("Usuń", delete)
	    
	}
}