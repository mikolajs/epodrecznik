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

class EditQuestSn extends BaseSlide {
    
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
        "tr" #> QuizQuestion.findAll(("authorId"->userId)~("subjectId"->subjectId)~
                ("departmentId"->departmentId)~("level"->level.toInt)).map(quest => {
            <tr id={quest._id.toString}><td>{Unparsed(quest.question)}</td>
            <td>{quest.answer}</td><td>
            {quest.fake.map(f => <span class="wrong">{f}</span>)}</td>
            <td>{quest.dificult}</td><td>{if(quest.public) "TAK" else "NIE"}</td></tr>
        })
    }
    
    //working ....
    def editQuest() = {
        printParam
        var id = ""
        var question = ""
        var public = false
        var answer = ""
        var wrongAnswers =""
        var dificult = "2"
            
        def save():JsCmd = {
            
            println("+++++++++++++++++++ SAVE QUEST ")
            printParam
            ///dodać test uprawnień
            val quest = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
            quest.authorId = 1L  //zmienić!!!!!!!!!!!!
            quest.answer = answer
            quest.fake = wrongAnswers.split(";").toList
            quest.question = question
            quest.subjectId = new ObjectId(subjectId)
            quest.departmentId = new ObjectId(departmentId)
            quest.dificult = tryo(dificult.toInt).openOr(9)
            quest.level = level.toInt
            quest.public = public
            quest.save
            JsFunc("editQuest.insertQuestion",quest._id.toString).cmd
        }
        
        def delete():JsCmd = {
             println("+++++++++++++++++++ Del QUEST ")
            QuizQuestion.find(id) match {
                case Some(quest) => {
                    quest.delete
                    JsFunc("editQuest.deleteQuestion", quest._id.toString).cmd
                }
                case _ => Alert("Nie znaleziono pytania!")
            }
        }

        val dificults = 2 to 6 map( i => {val iS = i.toString;(iS, iS)})        
        
       val form = "#idQuest" #> SHtml.text(id, id = _) &
        "#questionQuest" #> SHtml.textarea(question, x => question = x.trim) &
       "#answerQuest" #> SHtml.text(answer, x => answer = x.trim) &
       "#wrongQuest" #> SHtml.text(wrongAnswers, x => wrongAnswers = x.trim) &
       "#dificultQuest" #> SHtml.select(dificults, Full(dificult), dificult = _) &
       "#publicQuest" #> SHtml.checkbox(public, public = _, "id"->"publicQuest") &
       "#saveQuest" #> SHtml.ajaxSubmit("Zapisz",save) &
       "#deleteQuest" #> SHtml.ajaxSubmit("Usuń",delete) andThen SHtml.makeFormsAjax
       
        "form" #> (in => form(in))
       
    }
 
    
    private def printParam = println("subjectId="+ subjectId + "departmentId=" + departmentId + " level=" + level)

}