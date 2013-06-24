package pl.brosbit.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import pl.brosbit.model._
import mapper.By
import http.js.JsCmds.SetHtml
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import  _root_.net.liftweb.http.js.JsCmds._
import  _root_.net.liftweb.http.js.JsCmd
import  _root_.net.liftweb.http.js.JE._
import com.sun.org.omg.CORBA._IDLTypeStub

class ShowQuestionSn {
    
    val user = User.currentUser.open_!
    
    def showAllUserQuizes() = {
        val userId = user.id.is
        
        "tr" #> Quiz.findAll("authorId"->userId).map(quiz => {
            <tr><td><a href={"/resources/editquiz/"+ quiz._id.toString}>{quiz.title}</a></td>
            <td>{quiz.departmentInfo}</td><td>{quiz.subjectInfo}</td>
            <td>{quiz.questions.length.toString}</td><td>{quiz.subjectLev.toString}</td>
            <td>{quiz.description}</td>
            </tr>
        })
    }
    
    def showAllUserQuestions() = {
        val userId = user.id.is
         QuizQuestion.findAll("authorId"->userId).map(quest => {
             <tr><td>{quest.question}</td><td>{quest.answer}</td><td>{quest.fake.mkString(" :: ")}</td>
             <td>{quest.level.toString}</td><td>{quest.dificult.toString}</td><td>{quest.public.toString}</td></tr>
         })
    }
    
    def showAllUserAndPublicQuest() = {
         val userId = user.id.is
         //wrong question - howto or query?????
         QuizQuestion.findAll(("authorId"->userId)~("public"->true)).map(quest => {
             <tr><td>{quest.question}</td><td>{quest.answer}</td><td>{quest.fake.mkString(" :: ")}</td>
             <td>{quest.level.toString}</td><td>{quest.dificult.toString}</td><td>{quest.public.toString}</td></tr>
         })
    }
}
