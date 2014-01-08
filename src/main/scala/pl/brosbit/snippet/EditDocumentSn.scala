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
import http.js.{JsCmds, JsCmd}
import http.js.JsCmds.{ SetHtml, Alert, Run}
import http.js.JE._

class EditDocumentSn extends BaseSlide {

    val userId = User.currentUser.openOrThrowException("Niezalogowny nauczyciel").id.is
    
    val id = S.param("id").openOr("0")
    var document = if (id != "0") Document.find(id).getOrElse(Document.create) else Document.create
    
    
    val isOwner = document.ownerID == 0L || document.ownerID == userId || User.currentUser.openOrThrowException("Niezalogowany nauczyciel").role == "a"

    def editData() = {
    	
        var docID = id
        var title = document.title
        var descript = document.descript
        var departmentId = document.departmentId.toString
        var subjectId = document.subjectId.toString
        var level = document.level.toString
        var docContent = document.content

        def save() {
            //println("-------------save------------title-----------------")
            if (isOwner) {
                document.title = title
                document.descript = descript
                Department.find(departmentId) match {
                    case Some(dep) => {
                        document.departmentId = dep._id
                        document.departmentName = dep.name
                        document.subjectId = dep.subject
                        document.level = level.toInt
                        document.subcjectName = Subject.find(dep.subject).getOrElse(Subject.create).full
                        val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
                        document.ownerID = user.id.is
                        document.ownerName = user.fullName
                        document.content = docContent
                        document.save                       
                        if (id == "0") S.redirectTo("/resources/editdocument/"+ document._id.toString)
                        Alert("Zapisano")
                    }
                    case _ =>
                }
            } 
        }
        
        def delete() = {
            if(isOwner) {
                document.delete
            }
        }

        val subjects = Subject.findAll.map(sub => (sub._id.toString, sub.full))
        "#docID" #> SHtml.text(docID, docID = _) &
            "#docTitle" #> SHtml.text(title, title = _) &
            "#docDescription" #> SHtml.textarea(descript, descript = _) &
            "#subjects" #> SHtml.select(subjects, Full(subjectId), subjectId = _) &
            "#departmentHidden" #> SHtml.text(departmentId, departmentId = _, "type"->"hidden") &
            "#departments" #> departmentSelect() &
            "#docLevel" #> SHtml.select(levList, Full(level), level = _) &
            "#docContent" #> SHtml.textarea(docContent, d => docContent =  d.trim) & 
            "#docSave" #> SHtml.submit("Zapisz", save) &
            "#docDelete" #> SHtml.submit("Usuń", delete)
    }
    
    

   /* //action add new chapter
    def formEditChapter() = {
        var chapterTitle = ""
        var chapterLevel = ""
        var chapterId = ""
        var chapterIndex = ""
        var chapterContent = ""
        var isNew = false

        def save():JsCmd = {
            if(document.ownerID == 0L) {
                println("-------------no owner or no document!!----------")
                return Alert("Zapisz najpierw tytuł dokumentu")
            }
            var saved = "Zapisano"
            println("-------------save------------new--chapter----------" + chapterTitle)
            if (isOwner) {
                var chapter = DocumentChapter.find(chapterId).getOrElse(DocumentChapter.create)
                var where = try {
                    //println("chapter index = " + chapterIndex )
                    var i = tryo(chapterIndex.trim().toInt).getOrElse(0)
                    if (i > document.chapters.size) document.chapters.size else i
                } finally { 0 }
                var isNew_? = chapter.title == ""
                chapter.title = chapterTitle
                chapter.content = chapterContent
                chapter.document = document._id
                chapter.save
                val newChapters = document.chapters.filter(ch => ch != chapter._id)
                document.chapters = (document.chapters.take(where) :+ chapter._id) ::: document.chapters.drop(where)
                document.save
                JsFunc("editDoc.closeAfterEdit", chapter._id.toString).cmd
                
            } else {
                saved = "Brak uprawnień"
                S.redirectTo("/resources/documents")
                Alert("Brak uprawnień")
            }
        }
        
        def delete() = {
            println("---------------------no----book-------------!!!!!!!!!!!----")
            JsFunc("alert","Usuwanie!").cmd
        }

        val list = List(("0", "0"), ("1", "1"), ("2", "2"), ("3", "3"), ("4", "4"))
        val form =  "#chapterIDForm" #> SHtml.text(chapterId, chapterId = _, "style" -> "display:none;") &
            "#chapterIndexForm" #> SHtml.text(chapterIndex, chapterIndex = _, "style" -> "display:none;") &
            "#chapterTitleForm" #> SHtml.text(chapterTitle, chapterTitle = _) &
            "#chapterLevelForm" #> SHtml.select(list, Full("0"), chapterLevel = _) &
            "#chapterContentForm" #> SHtml.textarea(chapterContent, chapterContent = _) &
            "#saveChapterForm" #> SHtml.ajaxSubmit("Zapisz", save) &
            "#deleteChapterForm" #> SHtml.ajaxSubmit("Usuń", delete) andThen SHtml.makeFormsAjax

        "form" #> (in => form(in))
    } */

}
