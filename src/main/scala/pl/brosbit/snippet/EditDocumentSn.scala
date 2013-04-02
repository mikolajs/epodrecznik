package pl.brosbit.snippet

import java.util.Date
import scala.xml.{ Text, XML, Unparsed, Elem, Null, TopScope }
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
import http.js.JsCmds.{ SetHtml, Alert, Run }

class EditDocumentSn extends BaseSlide {
  val id = S.param("id").openOr("0")
  var document = if (id != "0") Document.find(id).getOrElse(Document.create) else Document.create
  
  val userId = User.currentUser match {
    case Full(u) => u.id.is
    case _ => -1L
  }
  val isOwner = if (document.ownerID == 0L || document.ownerID == userId) true else false


  def editMainData() = {
    var title = document.title
    var descript = document.descript
    var departmentId = document.departmentId.toString
    var subjectId = document.subjectId.toString            
        
    def save() = {
      //println("-------------save------------title-----------------")
      if (isOwner) {
        document.title = title
        document.descript = descript
        Department.find(departmentId) match {
            case Some(dep) => {
                document.departmentId = dep._id
                document.departmentName = dep.name
                document.subjectId = dep.subject
                document.subcjectName = Subject.find(dep.subject).getOrElse(Subject.create).full
                document.save
                Alert("Zapisano")
            }
            case _ => Alert("Nieprawidłowy dział!")
        }   
      } else {
    	  Alert("Nie zapisano!")
        //println("----------------------no----boook-------------!!!!!!!!!!!----")
      }
      
    }
    
    val subjects = Subject.findAll.map(sub => (sub._id.toString, sub.full))
    val form = "#docTitle" #> SHtml.text(title, title = _) &
      "#docDescription" #> SHtml.textarea(descript, descript = _) &
      "#docSubject" #> SHtml.select(subjects, Full(subjectId), subjectId = _) &
      "#departmentHidden" #> SHtml.text(departmentId, departmentId = _, "type"->"hidden") &
      "#departments" #> departmentSelect() &
      "#docSave" #> SHtml.ajaxSubmit("Zapisz", save) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }
  

  //action add new chapter
  def newChapter() = {
    var chapterTitle = ""
    var chapterIndex = ""
    val list = (1 to 30).map(i => (i.toString, i.toString))
    def save() {
      var saved = "Zapisano"
      println("-------------save------------new--chapter----------" + chapterTitle)
      if (isOwner) {
        var where = try {
          //println("chapter index = " + chapterIndex )
          var i = chapterIndex.trim().toInt - 1
          if (i > document.chapters.size) document.chapters.size else i
        } finally { 0 }
        
  
        val newChapter = Chapter("","",1)

        document.chapters = (document.chapters.take(where) :+ newChapter) ::: document.chapters.drop(where)
        document.save
      } else {
        saved = "Brak uprawnień"
        println("---------------------no----book-------------!!!!!!!!!!!----")
      }
      Alert(saved) 
    }

    val form = "#chapterTitle" #> SHtml.text(chapterTitle, chapterTitle = _) &
      "#chapterIndex" #> SHtml.select(list, Full("1"), chapterIndex = _) &
      "#saveAddChapter" #> SHtml.ajaxSubmit("Zapisz", save) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }


  //main data - chapter content
  def chapterData() = {
    
  }

  def formEditChapter() = {
    var title = ""
    var content = ""
    var level = ""
    var subchapterIndex = "-1"

    def save() {
     
    }

    def delete() {
     
    }
    
    val list = List(("0", "0"), ("1", "1"), ("2", "2"), ("3", "3"), ("4", "4"))
    val form = "#subchapterIndexForm" #> SHtml.text(subchapterIndex, subchapterIndex = _, "style" -> "display:none;") &
      "#chapterTitleForm" #> SHtml.text(title, title = _) &
      "#chapterLevelForm" #> SHtml.select(list, Full("0"), level = _) &
      "#editor" #> SHtml.textarea(content, content = _) &
      "#saveChapter" #> SHtml.submit("Zapisz", save) &
      "#deleteChapter" #> SHtml.submit("Usuń", delete) andThen SHtml.makeFormsAjax
      
    "formEditSubchapter" #> (in => form(in))
  }

}
