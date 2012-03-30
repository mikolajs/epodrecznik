package net.brosbit4u.snippet

import java.util.Date
import scala.xml.{ Text, XML, Unparsed }
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
  val chapterInt = try {val ch = chapter.toInt -1; if(ch < 0) 0 else ch } finally {1} 
  
  //ebooks => add new book and redirect to edit
  def newBook() = {
    var title = ""
    var description = ""
    def save() {
      User.currentUser match {
        case Full(user) => {
          val newBook = EBook.create
          newBook.title = title
          newBook.descript = description
          newBook.owner = user.fullName
          newBook.ownerID = user.id
          newBook.save
          S.redirectTo("/ebook/" + newBook._id.toString)
        }
        case _ => S.redirectTo("/user_mgt/login")
      }
    }
    "#title" #> SHtml.text(title, title = _) &
      "#description" #> SHtml.textarea(description, description = _) &
      "#save" #> SHtml.submit("Zapisz", save)
  }

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
        EBook.find(id) match {
          case Some(ebook) => {
             if (isOwner(ebook.ownerID)) {
            ebook.title = title
            ebook.descript = descript
            ebook.save
            }
             else saved = "Brak uprawnień"
          }
          case _ => {
            println("----------------------no----boook-------------!!!!!!!!!!!----")
            saved = "Nieudany zapis"
          }
        }
      
      Alert(saved) &
        Run("closeMkEditTitle()")
    }

    val form = "#bookTitle" #> SHtml.text(title, title = _) &
      "#bookDescription" #> SHtml.textarea(descript, descript = _) &
      "#saveEditBook" #> SHtml.ajaxSubmit("Zapisz", save) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))

  }
  //ebook => show chapter and subchapters tree and permisions and options for add chapters
  def ebookTree() = {
   var index = 0
    "li" #>  ebook.chapters.map(chapter => {
       index += 1
      <li><a href={"/ebook/" + id + "/" + index.toString }> {chapter.title}</a></li>
      })
  }

  def newChapter() = {
    var chapterTitle = ""
    var chapterIndex = ""
    val list = (1 to 30).map(i=>(i.toString,i.toString))
    
    def save(){
      var saved = "Zapisano"
      println("-------------save------------new--chapter----------")
        EBook.find(id) match {
          case Some(ebook) => {
             if (isOwner(ebook.ownerID)) {
            var where = try {
              //println("chapter index = " + chapterIndex )
              var i = chapterIndex.toInt - 1
              if (i  > ebook.chapters.size) ebook.chapters.size else i
              } finally {0}
            val chapter = Chapter(chapterTitle, "", Nil, Nil)

            ebook.chapters = (ebook.chapters.take(where) :+ chapter) ::: ebook.chapters.drop(where)
            ebook.save
            }
             else saved = "Brak uprawnień"
          }
          case _ => {
            println("----------------------no----book-------------!!!!!!!!!!!----")
            saved = "Nieudany zapis"
          }
        }
      
      Alert(saved) &
        Run("newChapter.insertAndClose()")
    }
    
    val form = "#chapterTitle" #> SHtml.text(chapterTitle, chapterTitle = _) &
      "#chapterIndex" #> SHtml.select(list, Full("1"), chapterIndex = _) &
      "#saveAddChapter" #> SHtml.ajaxSubmit("Zapisz", save) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }
  
  def showChapterPermission()= {
     if (chapterInt > ebook.chapters.size) {"li" #> <li>Nie ma takiego rozdziału!</li>}
     else {
        val id = User.currentUser.getOrElse(User.create).id
        val isOwn = isOwner(id)
        val editors = ebook.chapters(chapterInt ).permission
        "li" #> editors.map(editor => {
          <li>{editor.email} {if(isOwn){ SHtml.a(()=> { println("----------------delete--------permision-------------")
        	  											Run("permission.deleteUser(this)")},
        	  									Text("Usuń"))} else <a></a>}</li>
        })
     }
  }
  
  def formAddPermission() = {
    
    var mail = ""
    
    def mkAddPermission(){
      println("--------------------add -------permmission -----------------------")
      val u = User.findAll(By(User.email,mail))
      if (!u.isEmpty){
        val owner = Person(mail,u.head.id.is)
        val perm = ebook.chapters(chapterInt).permission
        ebook.chapters(chapterInt).permission = owner::perm 
        ebook.save
        println("--------------------add -------permmission ---------saved--------------")
      }
      if (!u.isEmpty) Alert("Dodano użytkownka: " + u.head.fullName) else Alert("Nie ma takiego użytkownika!") &
      Run("permission.addUser()")
    }
    
    val form = "#addPermissionMail" #> SHtml.text(mail, mail = _ ) &
    			"#addPermissionSubmit" #> SHtml.ajaxSubmit("Dodaj",mkAddPermission) andThen SHtml.makeFormsAjax
    "form" #> (in => form(in))
  }
  
  
  def chapterData() {
    val levelsList = List(<h2></h2> , <h3></h3> , <h4></h4>, <h5></h5>)
    val theChapter = ebook.chapters(chapterInt)
    "h2" #> <h2>{theChapter.title}</h2> &
    "#mainChapterContent" #> <div class="chapterContent">{theChapter.content}</div> &
    "#subchapterContent" #> theChapter.subchapters.map(sub => {
      val h = sub.level - 1
      "h3" #> //?????????????????//
    })
  }

  /*
  //ebook => show forms for edit
  def formEdit() = {
    if (!canEdit) S.redirectTo("/user_mgt/login", S.redirectTo("/ebooks"))

    var ID = if(id == "0") "0" else ebook._id.toString
    var title = ebook.title
    var descript = ebook.descript


    var i = -1;
    val listSubject = Subject.findAll.map(s => {i+= 1;(i.toString,s.full)})
    def saveData() {
      if (!canEdit) S.redirectTo("/user_mgt/login")
      //if not confirmed allow write to the same ebook!
      val newEBook =  EBook.create
        newEBook.title = title
        newEBook.descript = descript
        newEBook.save 

      S.redirectTo("/editable")
    }

    def deleteData() {

    }

    def cancelAction() {
      S.redirectTo("/editable")
    }

    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#titleEBook" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjectEBook" #> SHtml.select(listSubject,Full(descript),nr => descript = descript,"onchange"->"changeDepartmentSelect();") &
    "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,"title"->"Zapisz","onclick"->"return createData();") &
    "#delete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData,"title"->"Usuń") &
    "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction,"title"->"Anuluj") 
  } */

  def ebookList() = {
    val books = EBook.findAll
    "tbody" #> books.map(book => "a" #> <a href={"/ebook/" + book._id.toString} >{ book.title } </a> &
      "#descriptBook *" #> <p>{ book.descript }</p> &
      "#ownerBook *" #> <em>{ book.owner }</em>)
  }

  def canEdit: Boolean = {
    User.currentUser match {
      case Full(user) => true
      case _ => false
    }
  }

  def isOwner(userId: Long): Boolean = {
    User.currentUser match {
      case Full(user) => if (user.id.is == userId) true else false
      case _ => false
    }
  }

  def formTitle = {
    var title = ""
    var ID = ""
    var index = ""
    var level = "1"
    val listLevels = (1 to 4).map(x => (x.toString, x.toString))
    "#id" #> SHtml.text(ID, ID = _, "type" -> "hidden") &
      "#index" #> SHtml.text(index, index = _, "type" -> "hidden") &
      "#title" #> SHtml.text(title, title = _) &
      "#level" #> SHtml.select(listLevels, Full(level), nr => level = nr) &
      "#save" #> SHtml.ajaxSubmit("Zapisz", () => {
        println("++++++++++++++++++++++++ tytuł " + title + " id " + ID + " index " + index + " level " + level)
        Alert("SAVE")
      }) &
      "#delete" #> SHtml.ajaxSubmit("Usuń", () => {
        println("----------------------- tytuł " + title + " id " + ID + " index " + index + " level " + level)
        Alert("Delete")
      }) andThen SHtml.makeFormsAjax
  }

  def formAddPermision() {
    "#nic" #> ""
  }

  def formText() = {
    var ID = ""
    var index = ""
    var content = ""
    def saveData() {

    }
    "#id" #> SHtml.text(ID, ID = _, "type" -> "hidden") &
      "#index" #> SHtml.text(index, index = _, "type" -> "hidden") &
      "#editor" #> SHtml.text(content, content = _) &
      "#save" #> SHtml.ajaxSubmit("Zapisz", saveData, "title" -> "Zapisz", "onclick" -> "return createData();") andThen SHtml.makeFormsAjax
  }

  def testAjax2 = {
    var title = ""
    var ID = ""
    var index = ""
    var level = "1"
    val listLevels = (1 to 4).map(x => (x.toString, x.toString))
    val form = "#id" #> SHtml.text(ID, ID = _, "type" -> "hidden") &
      "#index" #> SHtml.text(index, index = _, "type" -> "hidden") &
      "#title" #> SHtml.text(title, title = _) &
      "#level" #> SHtml.select(listLevels, Full(level), nr => level = nr) &
      "#save" #> SHtml.ajaxSubmit("Save", () => {
        println("++++++++++++++++++++++++ tytuł " + title + " id " + ID + " index " + index + " level " + level)
        Alert("SAVE")
      }) &
      "#delete" #> SHtml.ajaxSubmit("Delete", () => {
        println("----------------------- tytuł " + title + " id " + ID + " index " + index + " level " + level)
        Alert("Delete")
      }) andThen SHtml.makeFormsAjax

    "#test2" #> (in => form(in))
  }

}
