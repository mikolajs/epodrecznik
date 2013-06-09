package pl.brosbit.snippet

import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import Helpers._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import  _root_.net.liftweb.http.js.JsCmds._
import  _root_.net.liftweb.http.js.JsCmd
import  _root_.net.liftweb.http.js.JE._

class EditQuizSn extends BaseSlide {
    
    var subjectId = S.param("sub").openOr("0")
    var departmentId = S.param("dep").openOr("0")
    var level = S.param("lev").openOr("4")
   if(subjectId == "0") {
         subjectId = Subject.findAll.head._id.toString
    }
   if(departmentId == "0") {
       departmentId = Department.findAll(("subject" -> subjectId)).head._id.toString
   } 
    
    def choiseQuest() = {
         val subjects = Subject.findAll.map(s => (s._id.toString, s.full))
         val levels = List(("1","I"),("2","II"),("3","III"),("4","IV"),("5","V"))
         def makeChoise() {
             S.redirectTo("/resources/editquest?sub="+subjectId+"&dep="+departmentId+"&lev="+level)
         }
         
        "#subjects" #> SHtml.select(subjects, Full(subjectId), subjectId = _) &
        "#departmentHidden" #> SHtml.text(departmentId, departmentId = _, "type"->"hidden") &
        "#departments" #> departmentSelect() &
        "#levels" #> SHtml.select(levels, Full(level), level = _) &
        "#choise" #> SHtml.submit("Wybierz", makeChoise)
    }
    
    def showQuests() = {
        val userId = 1L // User.currentUser.open_!.id.is
        println("departmentId: " + departmentId + "   subjectId: " + subjectId + " level: ")
        ".questionDiv" #> QuizQuestion.findAll(("authorId"->userId)~("subjectId"->subjectId)~
                ("departmentId"->departmentId)~("level"->level.toInt)).map(quest => {
            <tr id={quest._id.toString}><td>{Unparsed(quest.question)}</td>
            <td>{quest.answer}</td><td>
            {quest.fake.map(f => <span class="wrong">{f}</span>)}</td>
            <td>{quest.dificult}</td><td>{if(quest.public) "TAK" else "NIE"}</td></tr>
        })
    }
    
    
    //working ....
    def editQuiz() = {
        printParam
        var id = ""
        var questions = ""
        var public = false
        var description = ""
        var title = ""
            
        def save():JsCmd = {
            
            JsFunc("").cmd
        }
        
        def delete():JsCmd = {
            
            Alert("Nie znaleziono testu!")
         
        }
  
        
       val form = "#idQuiz" #> SHtml.text(id, id = _) &
       "#titleQuiz" #> SHtml.text(title, x => title = x.trim) &
       "#descriptionQuiz" #> SHtml.textarea(description, x => description = x.trim) &
       "#questionsQuiz" #> SHtml.text(questions, x => questions = x.trim) &
       "#publicQuiz" #> SHtml.checkbox(public, public = _, "id"->"publicQuest") &
       "#saveQuest" #> SHtml.ajaxSubmit("Zapisz",save) &
       "#deleteQuest" #> SHtml.ajaxSubmit("Usuń",delete) andThen SHtml.makeFormsAjax
       
        "form" #> (in => form(in))
       
    }
 
    
    private def printParam = println("subjectId="+ subjectId + "departmentId=" + departmentId + " level=" + level)

}