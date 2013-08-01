package pl.brosbit.snippet

import java.util.Date
import scala.xml.{ Text, XML, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class EditHeadwordSn extends BaseSlide with RoleChecker {
    val id = S.param("id").openOr("0")
    val dictionary = if (id != "0") Dictionary.find(id).getOrElse(Dictionary.create) else Dictionary.create

    def headwordData() = {
        "#slideHTML" #> Unparsed(dictionary.content)
    }

    def formEdit() = {

        var ID = if (id == "0") "0" else dictionary._id.toString
        var headword = dictionary.headword
        var subjectId = if (dictionary.subjectName != "") dictionary.subject.toString else ""
        var subjectName = dictionary.subjectName
        var data = dictionary.content
        var level = dictionary.level.toString

        val listSubject = Subject.findAll.map(s => { (s._id.toString, s.full) })

        def saveData() {

            val contentHtml = Unparsed(data)

            dictionary.headword = headword
            dictionary.subject = new ObjectId(subjectId)
            val sub = Subject.find(new ObjectId(subjectId)).getOrElse(Subject.create);
            dictionary.subjectName = sub.full
            val user =  User.currentUser.get
            val userID = user.id.is
            if(user.role == "a") dictionary.draft = false
            else {
                dictionary.authorId = userID
                dictionary.authorName = user.fullName
            }
            dictionary.content = contentHtml.toString
            dictionary.level = tryo(level.toInt).openOr(3)
            dictionary.save

            //S.redirectTo(makeURL) //!important must refresh page
        }

        def deleteData() {
            val user =  User.currentUser.get
            val userID = user.id.is
            if((ID != "0") && ((userID == dictionary.authorId) && dictionary.draft)|| user.role == "a") {
                dictionary.delete
                S.redirectTo("/resources/headwords")
            }      
        }

        def cancelAction() {
            S.redirectTo(makeURL)
        }

        val levels = List(("1","łatwy"),("2","średni"),("3","pełny"))
        
        "#id" #> SHtml.text(ID, ID = _, "type" -> "hidden") &
            "#headword" #> SHtml.text(headword, x => headword = x.trim, "class" -> "Name") &
            "#subjects" #> SHtml.select(listSubject, Full(subjectId), subjectId = _) &
            "#level" #> SHtml.select(levels, Full(level), level = _) &
            "#contentData" #> SHtml.text(data, data = _, "type" -> "hidden") &
            "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData, "title" -> "Zapisz") &
            "#delete" #> (if (isModerator) SHtml.button(<img src="/images/delico.png"/>,
                deleteData, "title" -> "Usuń")
            else <span></span>) &
            "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction, "title" -> "Anuluj")
    }

    def subjectList() = {
        val subjects = Subject.findAll
        "#subjectList" #> subjects.map(subject => 
            	"a" #> <a href="#" onclick="selectSubject(this)">{ subject.full } </a>)
    }

    private def makeURL = "/dictionary?s=" + dictionary._id.toString + "&w=" + dictionary.headword

}