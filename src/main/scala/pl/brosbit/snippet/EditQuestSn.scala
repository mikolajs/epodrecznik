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
    var departmentInfo = ""
    var level = S.param("lev").openOr("4")
   if(subjectId == "0") {
         subjectId = Subject.findAll match {
             case x::rest => x._id.toString
             case _ => ""
         }
    }
   if(departmentId == "0") {
            Department.findAll(("subject" -> subjectId)) match {
               case dep::rest => {
                 departmentId =  dep._id.toString
                  departmentInfo = dep.name
               }
               case _ =>
           } 
   }  
   else Department.find(departmentId) match {
       case Some(dep) => departmentInfo = dep.name
       case _ =>
   }
    
    def choiseQuest() = {
         val subjects = Subject.findAll.map(s => (s._id.toString, s.full))
         def makeChoise() {
             S.redirectTo("/resources/editquest?sub="+subjectId+"&dep="+departmentId+"&lev="+level)
         }
         
        "#subjects" #> SHtml.select(subjects, Full(subjectId), subjectId = _) &
        "#departmentHidden" #> SHtml.text(departmentId, departmentId = _, "type"->"hidden") &
        "#departments" #> departmentSelect() &
        "#levels" #> SHtml.select(levList, Full(level), level = _) &
        "#choise" #> SHtml.submit("Wybierz", makeChoise)
    }
    
    def showQuests() = {
        val userId = User.currentUser.openOrThrowException("Niezalogowany nauczyciel").id.is
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
            //println("+++++++++++++++++++ SAVE QUEST ")
            //printParam
            ///dodać test uprawnień
            val userId = User.currentUser.openOrThrowException("Niezalogowany nauczyciel").id.is
            val quest = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
            if(quest.authorId != 0L && quest.authorId != userId) return Alert("To nie twoje pytanie!")
            quest.authorId = userId
            quest.answer = answer
            quest.fake = wrongAnswers.split(";").toList
            quest.question = question
            quest.subjectId = new ObjectId(subjectId)
            quest.departmentId = new ObjectId(departmentId)
            quest.departmentInfo = departmentInfo
            quest.dificult = tryo(dificult.toInt).openOr(9)
            quest.level = level.toInt
            quest.public = public
            quest.save
            JsFunc("editQuest.insertQuestion",quest._id.toString).cmd
        }
        
        def delete():JsCmd = {
             println("+++++++++++++++++++ Del QUEST ")
            val userId = User.currentUser.openOrThrowException("Niezalogowany nauczyciel").id.is 
            QuizQuestion.find(id) match {
                case Some(quest) => {
                    if(quest.authorId == userId) {
                        quest.delete
                        JsFunc("editQuest.deleteQuestion", quest._id.toString).cmd
                    }
                    else Alert("To nie twoje pytanie!")
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