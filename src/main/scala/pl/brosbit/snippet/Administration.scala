

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
        "#departmentFormSubmit" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save">Zapisz</span>, saveData, 
                "onclick" -> "return isValid(this);") &
        "#departmentFormDelete" #> SHtml.button(<span class="glyphicon glyphicon-floppy-remove">Usuń</span>, deleteData)
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
      "#subjectFormSubmit" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save">Zapisz</span>, saveData,
              "onclick" -> "return isValid(this);") &
      "#subjectFormDelete" #> SHtml.button(<span class="glyphicon glyphicon-floppy-remove">Usuń</span>, deleteData)

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
      "#adminSubmit" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save">Zapisz</span>, 
              saveData, "onclick" -> "return isValid(this);")

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



