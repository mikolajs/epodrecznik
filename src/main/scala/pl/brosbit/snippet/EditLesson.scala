package pl.brosbit.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import pl.brosbit.lib._
import mapper.By
import json.DefaultFormats
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser._
import org.bson.types.ObjectId
import Helpers._

case class TestJ(l:String, t:String)

class EditLesson extends BaseSlide {
     

    def editLesson() = {
        var id = ""
        var json = ""
        var title = ""
        var public = false
        var courseId = ""
        var nr = ""
            
        id = S.param("id").openOr("0")
        val lesson = Lesson.find(id).getOrElse(Lesson.create)
        
        title = lesson.title
        public = lesson.public
        courseId = if(lesson.courseId.toString != "000000000000000000000000") lesson.courseId.toString else ""
        nr = lesson.nr.toString
        json = "[" + lesson.contents.map(cont => cont.forJSONStr).mkString(", ") + "]"
        
        val userId = User.currentUser.openOrThrowException("Niezalogowany nauczyciel").id.is   
        
        def save() {
            
            if(lesson.authorId == 0L || lesson.authorId == userId){
                 lesson.title = title
                 lesson.authorId = userId
                 lesson.nr = tryo(nr.toInt).openOr(0)
                 lesson.public = public
                 Course.find(courseId) match {
                     case Some(cour) => {
                         lesson.courseId = cour._id                      
                         }
                     case _ => println("BŁĄD nieznaleziono kursu") 
                 }
                lesson.contents = createLessonContentsList(json)
                if(lesson.public) checkLastAddedAndAppend(lesson)
                lesson.save 
            }   
            S.redirectTo("/resources/lessons")
        }
        
        def delete() {
//           Lesson.find(id) match {
//               case Some(less) => if(isLessonOwner(less)) less.delete
//               case _ =>
//           }
           if(isLessonOwner(lesson)) lesson.delete
           S.redirectTo("/resources/lessons")
        }
        
        val publics = List(("TAK","TAK"),("NIE","NIE"))
        val levels = List(("1","I"),("2","II"),("3","III"),("4","IV"),("5","V"))
        val courses = Course.findAll("authorId" -> userId).map(c => (c._id.toString, c.getInfo))
        
        "#ID" #> SHtml.text(id, id = _) &
        "#title" #> SHtml.text(title, x => title = x.trim) &
        "#nr" #> SHtml.text(nr, nr = _) &
        "#courses" #> SHtml.select(courses, Full(courseId), courseId = _) &
        "#public" #> SHtml.checkbox(public, public = _) &
        "#json" #> SHtml.text(json, json = _) &
        "#save" #> SHtml.submit("Zapisz", save ) &
        "#delete" #> SHtml.submit("Usuń!", delete, "onclick"->"return confirm('Na pewno usunąć całą lekcję?')" ) 
    }
    
    
    private def checkLastAddedAndAppend(lesson:Lesson){
	      
	          val lastAddedInDBList = LastAdded.findAll
	          val lastAdded = if(lastAddedInDBList.isEmpty) LastAdded.create else lastAddedInDBList.head
          
	          val link = "/lessons/" + lesson._id.toString	   
	          
	          val courseInfo = Course.find(lesson.courseId).getOrElse(Course.create).getInfo
	          val newLastAddedItem = LastAddedItem(lesson.title, courseInfo, "/lessons", Formater.formatDate(new Date()))
	          
	          var newLastAddedContent = lastAdded.content.filter(content => content.link != link )
	          newLastAddedContent =  newLastAddedItem::newLastAddedContent
	          if (newLastAddedContent.length > 15) newLastAddedContent = newLastAddedContent.dropRight(1)
	          lastAdded.content = newLastAddedContent
	          lastAdded.save
	 }
    
    def isLessonOwner(lesson:Lesson) = {
        User.currentUser match {
            case Full(user) => user.id.is == lesson.authorId
            case _ => false
        }       
    }
    
    def createLessonContentsList(jsonStr:String) = {
        implicit val formats = DefaultFormats
        val json = parse(jsonStr)
        json.extract[List[LessonContent]]
    }
    
    
}