package net.brosbit4u.snippet

import java.util.Date
import scala.xml.{ Text, XML, Unparsed, Elem, Null, TopScope }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import _root_.net.brosbit4u.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import http.js.JsCmds.{ SetHtml, Alert, Run }

class EBookSn {
  val id = S.param("id").openOr("0")
  val index = S.param("index").openOr("-1")
  val chapter = S.param("chapter").openOr("1")
  var ebook = if (id != "0") EBook.find(id).getOrElse(EBook.create) else EBook.create
  val chapterInt = try { val ch = chapter.toInt - 1; if (ch < 0) 0 else ch } finally { 1 }
  val userId = User.currentUser match {
    case Full(u) => u.id.is
    case _ => -1L
  }
  val isOwner = if (ebook.ownerID.toLong == userId) true else false
  //poniżej wywala się gdy czyta nie właściciel gdy nie ma jeszcze dodanego żadnego rozdziału
  val canEdit = if(isOwner) true else !(ebook.chapters(chapterInt).permission.filter(p => p.id.toLong == userId).isEmpty)

  //ebook => show title and description
  def ebookData() = {
    "h1 *" #> Text(ebook.title) &
    "p" #> <p>{ ebook.descript }</p> &
    "em" #> <em>{ ebook.owner }</em>
  }

  def editBookTitle() = {
    var title = ""
    var descript = ""
    var saved = "Zapisano"
    def save() = {

      println("-------------save------------title-----------------")
      if (isOwner) {
        ebook.title = title
        ebook.descript = descript
        ebook.save
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
  
  //ebook => show chapter and subchapters tree and permisions and options for add chapters
  def ebookTree() = {
    var index = 0
    "li" #> ebook.chapters.map(chapter => {
      index += 1
      <li><a href={ "/ebook/" + id + "/" + index.toString }> { chapter.subchapters.head.title }</a></li>
    })
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
          if (i > ebook.chapters.size) ebook.chapters.size else i
        } finally { 0 }
        
        val subchapter = SubChapter(chapterTitle, "Pusty", 0)
        val newChapter = Chapter(subchapter :: Nil, Nil)

        ebook.chapters = (ebook.chapters.take(where) :+ newChapter) ::: ebook.chapters.drop(where)
        ebook.save
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
  def showChapterPermission() = {
    if (chapterInt >= ebook.chapters.size) {
      "li" #> <span></span> &
        "#addPermission" #> <span></span>
    } else {

      if (isOwner) {
        val editors = ebook.chapters(chapterInt).permission
        "li" #> editors.map(editor => {
          <li>{ editor.email } {
            SHtml.a(() => {
              println("----------------delete--------permision-------------")
              ebook.chapters(chapterInt).permission = editors.filterNot(e => e.email == editor.email)
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
        val owner = Person(mail, u.head.id.is)
        val perm = ebook.chapters(chapterInt).permission
        if (!perm.exists(p => p.email == mail)) ebook.chapters(chapterInt).permission = owner :: perm
        ebook.save
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
    if (ebook.chapters.size <= chapterInt) {
      "#chapter" #> <h3> ROZDZIAŁ NIE ISTNIEJE! </h3> &
        "#addSubchapter" #> <span></span>
    } else {
      val theChapter = ebook.chapters(chapterInt)
      "#chapter" #> theChapter.subchapters.map(sub => {
        val l = sub.level + 2
        "h2" #> Elem(null, "h" + l.toString, Null, TopScope, Text(sub.title)) &
          "#subChapterContent" #> <div class="chapterContent">{ sub.content }</div> &
          "img" #> (if (canEdit) <img src="images/addico.png" onclick="editSubchapter"/> else <span></span>)

      }) &
        "#addSubchapter" #> (if (canEdit) <img src="images/addico.png" id="addSubchapter"/> else <span></span>)
    }
  }

  def formEditChapter() = {
    var title = ""
    var content = ""
    var level = ""
    var subchapterIndex = "-1"

    def save() {
      val subChIndInt = try { subchapterIndex.trim.toInt } finally { -1 }
      val levelInt = try { level.trim.toInt } finally { -1 }
      if (subChIndInt >= 0 && levelInt >= 0) {
        if (subChIndInt == 0 && levelInt == 0 && canEdit) {
          val subchapt = SubChapter(title,content,levelInt)
          ebook.chapters(chapterInt).subchapters = ebook.chapters(chapterInt).subchapters.updated(subChIndInt,subchapt)
          ebook.save
        }
      }
    }

    def delete() {
      val subChIndInt = try { subchapterIndex.trim.toInt } finally { -1 }
      val levelInt = try { level.trim.toInt } finally { -1 }
      if (subChIndInt > 0 && levelInt > 0) {
        //it mean delete chapter but index must be 0
        if (subChIndInt == 0 ) {
          if (levelInt == 0 && isOwner) {
            ebook.chapters = ebook.chapters.take(chapterInt) ::: ebook.chapters.drop(chapterInt + 1)
            ebook.save
          } //else error
        }
        else {
          //delete one subchapter
          if(levelInt > 0 && levelInt < 5 && canEdit){
            var sch =  ebook.chapters(chapterInt).subchapters
            sch = sch.take(subChIndInt) ::: sch.drop(subChIndInt + 1)
            ebook.chapters(chapterInt).subchapters = sch
            ebook.save
          } //else error
        }
      }
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
