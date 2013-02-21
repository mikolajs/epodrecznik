

package pl.brosbit.snippet

import java.util.Date
import pl.brosbit.lib.Formater
import _root_.net.liftweb._
import util._
import common._
import http.{ S, SHtml }
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import _root_.net.liftweb.mapper.By
import _root_.net.liftweb.json.JsonDSL._
import org.bson.types.ObjectId
import Helpers._
import scala.xml.Unparsed
import pl.brosbit.lib.MailConfig

class Administration {

  def usersList() = {
    val users = User.findAll.filter(u => isTeacher(u))
    "tbody" #> users.map(u => {
      <tr>
        <td id={"id" + u.id.toString}>{ u.lastName.is }</td><td>{ u.firstName.is }</td><td>{ u.email.is }</td>
        <td>{ u.role.is}</td>
      </tr>
    })
  }

  def usersForm() = {
    var lastName = ""
    var firstName = ""
    var email = ""
    var role = "t"

    def deleteData() {
      val users = User.findAll(By(User.email, email)).filter(u => isTeacher(u))
      if (users.nonEmpty) {
        users.foreach(u => {
          u.delete_!
        })
      }
    }

    def saveData() {
      val users = User.findAll(By(User.email, email)).filter(u => isTeacher(u))
      if (users.nonEmpty) {
        val u = users.head
        u.firstName(firstName).lastName(lastName).email(email).role(role).save
      } else {
        val u = User.create
        u.firstName(firstName).lastName(lastName).email(email).role(role).save
      }
    }
    val users = List(("t","nauczyciel"),("m", "moderator"))
    "#usersFormLastName" #> SHtml.text(lastName, lastName = _, "class" -> "Name", "maxlenght" -> "40") &
      "#usersFormFirstName" #> SHtml.text(firstName, firstName = _, "class" -> "Name", "maxlenght" -> "40") &
      "#usersFormEmail" #> SHtml.text(email, email = _, "class" -> "email", "maxlenght" -> "50") &
      "#usersFormLevel" #> SHtml.select(users, Full(role), role = _) &
      "#usersFormSubmit" #> SHtml.button(<img src="/images/saveico.png"/>, saveData, 
              "onclick" -> "return isValid(this);") &
      "#usersFormDelete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData)
  }

  def departmentList() = {
    val depart = Department.findAll
    "tbody" #> depart.map(d => {
      <tr id={ d._id.toString }><td>{ d.name }</td><td id={ d.subject.toString }>{ d.subFull }</td></tr>
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
        dep.save
      } else println("Error! Subject ID not found!")
    }

    val subjects = Subject.findAll.map(s => (s._id.toString, s.full))
    "#departmentFormId" #> SHtml.text(id, id = _, "type" -> "hidden") &
      "#departmentFormName" #> SHtml.text(name, name = _, "class" -> "name", "maxlenght" -> "20") &
      "#departmentFormSubject" #> SHtml.select(subjects,
        Full(if (subjects.isEmpty) "brak" else subjects.head._2), subject = _) &
        "#departmentFormSubmit" #> SHtml.button(<img src="/images/saveico.png"/>, saveData, 
                "onclick" -> "return isValid(this);") &
        "#departmentFormDelete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData)
  }

  def subjectList() = {
    val subject = Subject.findAll
    "tbody" #> subject.map(s => {
      <tr id={ s._id.toString }><td>{ s.full }</td></tr>
    })
  }

  def subjectForm() = {
    var full = ""
    var id = ""
    def deleteData() {
      val subjectOpt = Subject.find("_id" -> id)
      if (subjectOpt.isDefined) {
        subjectOpt.get.delete
      }
    }

    def saveData() {
      val sub = Subject.find("_id" -> id).getOrElse(Subject.create)
      sub.full = full
      sub.save
    }

    "#subjectFormName" #> SHtml.text(full, full = _, "class" -> "name", "maxlenght" -> "30") &
      "#subjectFormId" #> SHtml.text(id, id = _, "type" -> "hidden") &
      "#subjectFormSubmit" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,
              "onclick" -> "return isValid(this);") &
      "#subjectFormDelete" #> SHtml.button(<img src="/images/delico.png"/>, deleteData)

  }

  def dataAdmin() = {
    var email = ""
    var password = ""
    var admins = User.findAll(By(User.role, "a"))
    if (admins.nonEmpty) {
      val admin = admins.head
      email = admin.email.is
    }
    def saveData() {
      admins = User.findAll(By(User.role, "a"))
      if (admins.nonEmpty) {
        val admin = admins.head
        admin.email(email).password(password).save
      }
    }

    "#adminPassword" #> SHtml.text(password, password = _, "type" -> "password", 
            "class" -> "password", "maxlenght" -> "20") &
      "#adminEmail" #> SHtml.text(email, email = _, "class" -> "required email", 
              "maxlenght" -> "50", "class" -> "email") &
      "#adminSubmit" #> SHtml.button(<span>ZAPISZ! <img src="/images/saveico.png"/></span>, 
              saveData, "onclick" -> "return isValid(this);")

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
      "tr" #> <tr id={ item._id.toString() }><td>{ item.title }</td><td><img src={ item.imgPath }/></td>
      		<td>{ Unparsed(item.content) }</td><td>{ item.link }</td></tr>
    })
  }

 def mailConfigure() = {
    var host = ""
    var email = ""
    var password = ""
    val mailConf = new MailConfig
    val tuple = mailConf.getConfigTuple
    host = tuple._1
    email = tuple._2
    def save() = {     
      mailConf.configureMailer(host, email, password)
    }
    
      "#host" #> SHtml.text(host, host = _) &
      "#email" #> SHtml.text(email, email = _) &
      "#password" #> SHtml.text(password, password = _, "type" -> "password") &
      "#submit" #> SHtml.submit("Zapisz", save)
 }
 
 
 private def isTeacher(u:User) = u.role.is == "t" ||  u.role.is == "m"

}



