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
        var public = ""
        var subjectID = ""
        var level = ""
        var departmentID = ""
            
        id = S.param("id").openOr("0")
        val lesson = Lesson.find(id).getOrElse(Lesson.create)
        
        title = lesson.title
        public = if(lesson.public) "TAK" else "NIE"
        subjectID = if(lesson.subjectId != null) lesson.subjectId.toString else ""
        departmentID = if(lesson.departmentId != null) lesson.departmentId.toString else ""
        level = lesson.lev.toString      
        json = "[" + lesson.contents.map(cont => cont.forJSONStr).mkString(", ") + "]"
            
        def save() {
            val userId = User.currentUser.open_!.id.is
            var tLesson = Lesson.find(id).getOrElse(Lesson.create)
            if(tLesson.authorId == 0L || tLesson.authorId == userId){
                 tLesson.title = title
                 tLesson.authorId = userId
                 tLesson.lev = tryo(level.toInt).openOr(4)
                 tLesson.public = (public == "TAK") 
                 Subject.find(subjectID) match {
                     case Some(sub) => {
                         tLesson.subjectId = sub._id
                         tLesson.subjectInfo = sub.full
                         }
                     case _ => println("BŁĄD nieznaleziono przedmiotu") 
                 }
                 Department.find(departmentID) match {
                     case Some(dep) => {
                         tLesson.departmentId = dep._id
                         tLesson.departmentInfo = dep.name
                     }
                     case _ => 
                 }
                tLesson.contents = createLessonContentsList(json)
                if(tLesson.public) checkLastAddedAndAppend(tLesson)
                tLesson.save 
            }   
            S.redirectTo("/resources/lessons")
        }
        
        def delete() {
           Lesson.find(id) match {
               case Some(less) => if(isLessonOwner(less)) less.delete
               case _ =>
           }
           S.redirectTo("/resources/lessons")
        }
        
        val publics = List(("TAK","TAK"),("NIE","NIE"))
        val levels = List(("1","I"),("2","II"),("3","III"),("4","IV"),("5","V"))
        val subjects = Subject.findAll.map(s => (s._id.toString, s.full))
        
        "#ID" #> SHtml.text(id, id = _) &
        "#title" #> SHtml.text(title, x => title = x.trim) &
        "#subjects" #> SHtml.select(subjects, Full(subjectID), subjectID = _) &
        "#departmentHidden" #> SHtml.text(departmentID, departmentID = _) &
        "#departments" #> departmentSelect() &
        "#public" #> SHtml.select(publics, Full("TAK"), public = _) &
        "#level" #> SHtml.select(levels, Full("4"), level = _ ) &
        "#json" #> SHtml.text(json, json = _) &
        "#save" #> SHtml.submit("Zapisz", save ) &
        "#delete" #> SHtml.submit("Usuń!", delete, "onclick"->"return confirm('Na pewno usunąć całą lekcję?')" ) 
    }
    
    
    private def checkLastAddedAndAppend(lesson:Lesson){
	      
	          val lastAddedInDBList = LastAdded.findAll
	          val lastAdded = if(lastAddedInDBList.isEmpty) LastAdded.create else lastAddedInDBList.head
          
	          val link = "/lessons/" + lesson._id.toString	          
	          val newLastAddedItem = LastAddedItem(lesson.title, lesson.subjectInfo, "/lessons", Formater.formatDate(new Date()))
	          
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
        println(jsonStr) //testowo usunąć!!!!!!!!!!!!!!!!!!!!!!!!!!
        val json = parse(jsonStr)
        json.extract[List[LessonContent]]
    }
    
    
}