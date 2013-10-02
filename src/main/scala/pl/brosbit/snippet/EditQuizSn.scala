package pl.brosbit.snippet

import scala.xml.{ Text, XML, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import Helpers._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._

class EditQuizSn extends BaseSlide {

    var subjectId = S.param("sub").openOr("0")
    var departmentId = S.param("dep").openOr("0")
    var level = S.param("lev").openOr("4")
    if (subjectId == "0") {
        subjectId = Subject.findAll.head._id.toString
    }
    if (departmentId == "0") {
        departmentId = Department.findAll(("subject" -> subjectId)).head._id.toString
    }

    def choiseQuest() = {
        val subjects = Subject.findAll.map(s => (s._id.toString, s.full))
        val levels = List(("1", "I"), ("2", "II"), ("3", "III"), ("4", "IV"), ("5", "V"))
        def makeChoise() {
            S.redirectTo("/resources/editquiz?sub=" + subjectId + "&dep=" + departmentId + "&lev=" + level)
        }

        "#subjects" #> SHtml.select(subjects, Full(subjectId), subjectId = _) &
            "#departmentHidden" #> SHtml.text(departmentId, departmentId = _, "type" -> "hidden") &
            "#departments" #> departmentSelect() &
            "#levels" #> SHtml.select(levels, Full(level), level = _) &
            "#choise" #> SHtml.submit("Wybierz", makeChoise)
    }

    def questionList() = {
        val userId = User.currentUser.open_!.id.is

        val questions = QuizQuestion.findAll(("authorId" -> userId) ~ ("subjectId" -> subjectId) ~
            ("departmentId" -> departmentId) ~ ("level" -> level.toInt))

        ///dodać wyszukiwanie pytań z quizu i podział na dwie listy oraz wyświetlenie osobne   

        ".questLiAll" #> questions.map(quest => <li id={ quest._id.toString }>
                                                    <span class="question">{ Unparsed(quest.question) }</span>
                                                    <span class="rightAnswer">{ quest.answer }</span>
                                                    { quest.fake.map(f => <span class="wrong">{ f }</span>) }
                                                    <strong title="poziom trudności">{ quest.dificult }</strong>
                                                </li>)
    }

    //working ....
    def editQuiz() = {

        val idQuiz = S.param("id").openOr("0")
        var id = idQuiz
        var questions = ""
        var public = false
        var description = ""
        var title = ""
        val userId = User.currentUser.open_!.id.is

        Quiz.find(idQuiz) match {
            case Some(quiz) => {
                questions = quiz.questions.mkString(";")
                public = quiz.public
                description = quiz.description
                title = quiz.title
            }
            case _ =>
        }

        def save(): JsCmd = {
             println("========= save quiz ========")
            val quiz = Quiz.find(id).getOrElse(Quiz.create)
            if (quiz.authorId != 0L && quiz.authorId != userId && !User.currentUser.open_!.superUser)
                return Alert("To nie jest Twój test!")

            quiz.description = description
            if(quiz.authorId == 0L) quiz.authorId = userId
            quiz.title = title
            quiz.public = public
            quiz.subjectLev = tryo(level.toInt).openOr(0)
            quiz.departmentId = new ObjectId(departmentId)
            quiz.subjectId = new ObjectId(subjectId)
             
            Department.find(departmentId) match {
                case Some(dep) => quiz.departmentInfo = dep.name
                case _ =>
            }
             
            Subject.find(subjectId) match {
                case Some(sub) => quiz.subjectInfo = sub.full
                case _ =>
            }
            
            quiz.questions = questions.split(";").toList.map(q => new ObjectId(q))
            quiz.save
            S.redirectTo("/resources/quizes")
           Alert("Zapisano!")
        }

        def delete(): JsCmd = {
            println("========= delete quiz ========")
            Quiz.find(id) match {
                case Some(quiz) => {
                    if (quiz.authorId != 0L || userId == quiz.authorId || User.currentUser.open_!.superUser) {
                        quiz.delete
                        S.redirectTo("/resources/quizes")
                        Run("")
                    } else Alert("To nie jest Twój test!")
                }
                case _ => Alert("Nie znaleziono testu!")
            }
        }

        val form = 
            "#titleQuiz" #> SHtml.text(title, x => title = x.trim) &
            "#descriptionQuiz" #> SHtml.textarea(description, x => description = x.trim) &
            "#questionsQuiz" #> SHtml.text(questions, x => questions = x.trim) &
            "#publicQuiz" #> SHtml.checkbox(public, public = _, "id" -> "publicQuest") &
            "#saveQuiz" #> SHtml.ajaxSubmit("Zapisz", save) &
            "#deleteQuiz" #> SHtml.ajaxSubmit("Usuń", delete) andThen SHtml.makeFormsAjax

        "form" #> (in => form(in))

    }

    private def printParam = 
        println("subjectId=" + subjectId + "departmentId=" + departmentId + " level=" + level)

}