

package net.brosbit4u.snippet

import java.util.Date
import _root_.net.brosbit4u.lib.Formater
import _root_.net.liftweb._
import util._
import common._
import http.{ S, SHtml }
import mapper.{ OrderBy, Descending }
import net.brosbit4u.model._
import _root_.net.liftweb.mapper.By
import _root_.net.liftweb.json.JsonDSL._
import org.bson.types.ObjectId
import Helpers._
import scala.xml.Unparsed

class Administration {

  def usersList() = {
    val users = User.findAll.filter(u => u.level.is != 0)
    "tbody" #> users.map(u => {
      <tr>
        <td>{ u.lastName.is }</td><td>{ u.firstName.is }</td><td>{ u.email.is }</td>
        <td>{ u.level.is.toString }</td><td>{ u.karma.is.toString }</td>
      </tr>
    })
  }

  def usersForm() = {
    var lastName = ""
    var firstName = ""
    var email = ""
    var level = 9

    def deleteData() {
      val users = User.findAll(By(User.email, email)).filter(u => u.level.is != 0)
      if (users.nonEmpty) {
        users.foreach(u => {
          u.delete_!
        })
      }
    }

    def saveData() {
      val users = User.findAll(By(User.email, email)).filter(u => u.level.is != 0)
      if (users.nonEmpty) {
        val u = users.head
        u.firstName(firstName).lastName(lastName).email(email).level(level.toInt).save
      } else {
        val u = User.create
        u.firstName(firstName).lastName(lastName).email(email).level(level.toInt).karma(0).save
      }
    }
    val levels = (1 to 9).map(i => (i, i.toString))
    "#usersFormLastName" #> SHtml.text(lastName, lastName = _, "class" -> "Name", "maxlenght" -> "40") &
      "#usersFormFirstName" #> SHtml.text(firstName, firstName = _, "class" -> "Name", "maxlenght" -> "40") &
      "#usersFormEmail" #> SHtml.text(email, email = _, "class" -> "email", "maxlenght" -> "50") &
      "#usersFormLevel" #> SHtml.selectObj[Int](levels, Full(level), level = _) &
      "#usersFormSubmit" #> SHtml.button(<img src="/images/saveico.png"/>, saveData, "onclick" -> "return isValid(this);") &
      "#usersFormDelete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData)
  }

  def departmentList() = {
    val depart = Department.findAll
    "tbody" #> depart.map(d => {
      <tr id={ d._id.toString }><td>{ d.name }</td><td id={ d.subject.toString }>{ d.subFull }</td><td>{ d.subLev.toString }</td></tr>
    })
  }

  def departmentForm() = {
    var name = ""
    var subject = ""
    var id = ""

    def deleteData() {
      val departmentOpt = Department.find("_id" -> id)
      if (departmentOpt.isDefined) {
        departmentOpt.get.delete
      }
    }

    def saveData() {
      val dep = Department.find("_id" -> id).getOrElse(Department.create(new ObjectId(subject)))
      dep.name = name
      val subOpt = Subject.find("_id" -> subject.toString)
      if (subOpt.isDefined) {
        val sub = subOpt.get
        dep.subject = sub._id
        dep.subFull = sub.full
        dep.subLev = sub.lev
        dep.save
      } else println("Error! Subject ID not found!")
    }

    val subjects = Subject.findAll.map(s => (s._id.toString, s.full + " " + s.lev.toString))
    "#departmentFormId" #> SHtml.text(id, id = _, "type" -> "hidden") &
      "#departmentFormName" #> SHtml.text(name, name = _, "class" -> "name", "maxlenght" -> "20") &
      "#departmentFormSubject" #> SHtml.select(subjects,
        Full(if (subjects.isEmpty) "brak" else subjects.head._2), subject = _) &
        "#departmentFormSubmit" #> SHtml.button(<img src="/images/saveico.png"/>, saveData, "onclick" -> "return isValid(this);") &
        "#departmentFormDelete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData)
  }

  def subjectList() = {
    val subject = Subject.findAll
    "tbody" #> subject.map(s => {
      <tr id={ s._id.toString }><td>{ s.full }</td><td>{ s.lev.toString }</td></tr>
    })
  }

  def subjectForm() = {
    var full = ""
    var id = ""
    var levStr = ""

    def deleteData() {
      val levInt = levStr.toInt
      val subjectOpt = Subject.find("_id" -> id)
      if (subjectOpt.isDefined) {
        subjectOpt.get.delete
      }
    }

    def saveData() {
      val levInt = levStr.toInt
      val sub = Subject.find("_id" -> id).getOrElse(Subject.create)
      sub.full = full
      sub.lev = levInt
      sub.save
    }

    val levList = List(("1", "I"), ("2", "II"), ("3", "III"), ("4", "IV"))
    "#subjectFormName" #> SHtml.text(full, full = _, "class" -> "name", "maxlenght" -> "30") &
      "#subjectFormLevel" #> SHtml.select(levList, Full(levList.head._2), levStr = _) &
      "#subjectFormId" #> SHtml.text(id, id = _, "type" -> "hidden") &
      "#subjectFormSubmit" #> SHtml.button(<img src="/images/saveico.png"/>, saveData, "onclick" -> "return isValid(this);") &
      "#subjectFormDelete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData)

  }

  def dataAdmin() = {
    var email = ""
    var password = ""
    var admins = User.findAll(By(User.level, 0))
    if (admins.nonEmpty) {
      val admin = admins.head
      email = admin.email.is
    }
    def saveData() {
      admins = User.findAll(By(User.level, 0))
      if (admins.nonEmpty) {
        val admin = admins.head
        admin.email(email).password(password).save
      }
    }

    "#adminPassword" #> SHtml.text(password, password = _, "type" -> "password", "class" -> "password", "maxlenght" -> "20") &
      "#adminEmail" #> SHtml.text(email, email = _, "class" -> "required email", "maxlenght" -> "50", "class" -> "email") &
      "#adminSubmit" #> SHtml.button(<span>ZAPISZ! <img src="/images/saveico.png"/></span>, saveData, "onclick" -> "return isValid(this);")

  }

  def editLink() = {
    var id = ""
    var title = ""
    var content = ""
    var imgPath = ""
    var link = ""

    def deleteLink() {
      var news = Link.find(id).getOrElse(Link.create)
      if (id != "0") news.delete
      S.notice("Usunięto id=" + id)
    }

    def saveLink() {
      var news = Link.find(id).getOrElse(Link.create)
      if (title != "" && content != "") {
        val d = new Date()
        if (id == "" || id == "0") {
          news._id = d.getTime.toString
        }
        news.title = title
        news.content = Unparsed(content).toString()
        news.imgPath = imgPath
        news.link = link
        news.save
      }
    }

    "#editId" #> SHtml.text(id, id = _, "type" -> "hidden") &
      "#editTitle" #> SHtml.text(title, title = _) &
      "#pathImg" #> SHtml.text(imgPath, imgPath = _, "type" -> "hidden") &
      "#editContent" #> SHtml.textarea(content, content = _) &
      "#editLink" #> SHtml.text(link, link = _) &
      "#editDelete" #> SHtml.submit("Usuń", deleteLink) &
      "#editSave" #> SHtml.submit("Zapisz", saveLink)

  }

  def shortLink() = {
    val news = Link.findAll //OrderBy(Link.date, Descending)
    "tbody" #> news.map(item => {
      "tr" #> <tr id={ item._id.toString() }><td>{ item.title }</td><td><img src={ item.imgPath }/></td><td>{ Unparsed(item.content) }</td><td>{ item.link }</td></tr>
    })
  }

  def contact() = {
    var title = ""
    var email = ""
    var content = ""
    def save() = {
      //tu dodać wysyłanie Maila!!!!
    }

    "#title" #> SHtml.text(title, title = _) &
      "#email" #> SHtml.text(email, email = _) &
      "#contact" #> SHtml.textarea(content, content = _) &
      "#submit" #> SHtml.submit("WYŚLIJ", save)
  }

}



