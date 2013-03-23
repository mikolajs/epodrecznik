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

class EditArticleSn {
  val id = S.param("id").openOr("0")
  val index = S.param("index").openOr("-1")
  val chapter = S.param("chapter").openOr("1")
  var article = if (id != "0") Article.find(id).getOrElse(Article.create) else Article.create
  val chapterInt = try { val ch = chapter.toInt - 1; if (ch < 0) 0 else ch } finally { 1 }
  val userId = User.currentUser match {
    case Full(u) => u.id.is
    case _ => -1L
  }
  val isOwner = if (article.ownerID.toLong == userId) true else false
  //poniżej wywala się gdy czyta nie właściciel gdy nie ma jeszcze dodanego żadnego rozdziału
  val canEdit = if(isOwner) true else !(article.editors.filter(p => p.id.toLong == userId).isEmpty)

  //article => show title and description
  def articleData() = {
    "h1 *" #> Text(article.title) &
    "p" #> <p>{ article.descript }</p> &
    "em" #> <em>{ article.ownerName }</em>
  }

  def editBookTitle() = {
    var title = ""
    var descript = ""
    var saved = "Zapisano"
    def save() = {

      println("-------------save------------title-----------------")
      if (isOwner) {
        article.title = title
        article.descript = descript
        article.save
      } else {
        saved = "Brak uprawnień"
        println("----------------------no----boook-------------!!!!!!!!!!!----")
      }
      Alert(saved)
    }

    val form = "#bookTitle" #> SHtml.text(title, title = _) &
      "#bookDescription" #> SHtml.textarea(descript, descript = _) &
      "#saveEditBook" #> SHtml.ajaxSubmit("Zapisz", save) andThen SHtml.makeFormsAjax

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
          if (i > article.chapters.size) article.chapters.size else i
        } finally { 0 }
        
  
        val newChapter = Chapter("","",1)

        article.chapters = (article.chapters.take(where) :+ newChapter) ::: article.chapters.drop(where)
        article.save
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

  //admin can add user that can edit chapter
  def showPermission() = {
    if (chapterInt >= article.chapters.size) {
      "li" #> <span></span> &
        "#addPermission" #> <span></span>
    } else {

      if (isOwner) {
        val editors = article.editors
        "li" #> editors.map(editor => {
          <li>{ editor.email } {
            SHtml.a(() => {
              println("----------------delete--------permision-------------")
              article.editors = editors.filterNot(e => e.email == editor.email)
              Run("permission.deleteUser(this)")
            }, Text(" Usuń"))
          }</li>
        })
      } else {
        "li" #> <span></span> &
          "#addPermission" #> <span></span>
      }
    }
  }

  def formAddPermission() = {

    var mail = ""

    def mkAddPermission() {
      println("--------------------add -------permmission -----------------------")
      mail = mail.trim()
      val u = User.findAll(By(User.email, mail))
      if (!u.isEmpty) {
        val newEditor = Person(u.head.fullName, mail, u.head.id.is)
        if (!article.editors.exists(p => p.email == mail)) article.editors = newEditor::article.editors
        article.save
        println("--------------------add -------permission ---------saved--------------")
      }
      if (!u.isEmpty) Alert("Dodano użytkownka: " + u.head.fullName) else Alert("Nie ma takiego użytkownika!") &
        Run("permission.addUser()")
    }

    val form = "#addPermissionMail" #> SHtml.text(mail, mail = _) &
      "#addPermissionSubmit" #> SHtml.ajaxSubmit("Dodaj", mkAddPermission) andThen SHtml.makeFormsAjax
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
