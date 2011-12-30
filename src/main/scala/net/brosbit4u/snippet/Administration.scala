

package net.brosbit4u.snippet

import java.util.Date
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
//import _root_.scala.xml._
import http.{S,SHtml}
import mapper.{OrderBy,Descending}
import net.brosbit4u.model._
import _root_.net.liftweb.mapper.By
import _root_.net.liftweb.json.JsonDSL._
import org.bson.types.ObjectId
import Helpers._


class Administration {

    def usersList() = {
        val users = User.findAll.filter(u => u.level.is != 0)
        "tbody" #> users.map(u => {
                <tr><td>{u.lastName.is}</td><td>{u.firstName.is}</td><td>{u.email.is}</td>
                    <td>{u.level.is.toString}</td><td>{u.karma.is.toString}</td></tr>
            })
    }

    def usersForm() = {
        var lastName = ""
        var firstName = ""
        var email = ""
        var level = 9

        def deleteData(){
            val users = User.findAll(By(User.email,email)).filter(u => u.level.is != 0)
            if(users.nonEmpty){
                users.foreach(u => {
                        u.delete_!
                    })
            }
        }

        def saveData() {
            val users = User.findAll(By(User.email,email)).filter(u => u.level.is != 0)
            if(users.nonEmpty){
                val u = users.head
                u.firstName(firstName).lastName(lastName).email(email).level(level.toInt).save
            }
            else {
                val u = User.create
                u.firstName(firstName).lastName(lastName).email(email).level(level.toInt).karma(0).save
            }
        }
        val levels = (1 to 9).map(i => (i, i.toString))
        "#usersFormLastName" #> SHtml.text(lastName, lastName = _,"class"->"Name","maxlenght"->"40") &
        "#usersFormFirstName" #> SHtml.text(firstName, firstName = _,"class"->"Name","maxlenght"->"40") &
        "#usersFormEmail" #> SHtml.text(email, email = _,"class"->"email","maxlenght"->"50") &
        "#usersFormLevel" #> SHtml.selectObj[Int](levels, Full(level), level = _ ) &
        "#usersFormSubmit"  #> SHtml.button(<img src="/images/saveico.png" />,saveData,"onclick"->"return isValid(this);") &
        "#usersFormDelete" #> SHtml.button(<img src="/images/delico.png" />,deleteData)
    }

    def departmentList() = {
        val depart = Department.findAll
        "tbody" #> depart.map(d => {
                <tr><td>{d.name}</td><td>{d.short}</td><td>{d.subject}</td></tr>
            })
    }

    def departmentForm() = {
        var name = ""
        var short = ""
        var subject = ""

        def deleteData() {
            val departments = Department.findAll("short"-> short)
            if(departments.nonEmpty){
                departments.head.delete
            }
        }

        def saveData() {
            val departments = Department.findAll("short"-> short)
            if(departments.nonEmpty){
                val dep = departments.head
                dep.name = name
                dep.short = short
                dep.subject = subject
                dep.save
            }
            else {
                val dep = Department(new Date().getTime,short,name, subject)
                dep.save
            }
        }
        val subjects = Subject.findAll.map(s =>(s.short,s.full))
        "#departmentFormName" #> SHtml.text(name, name = _,"class"->"name","maxlenght"->"20") &
        "#departmentFormShort" #> SHtml.text(short, short = _,"class"->"name","maxlenght"->"9") &
        "#departmentFormSubject" #> SHtml.select(subjects, Full(subjects.head._2),subject = _) &
        "#departmentFormSubmit"  #> SHtml.button(<img src="/images/saveico.png" />,saveData,"onclick"->"return isValid(this);") &
        "#departmentFormDelete" #> SHtml.button(<img src="/images/delico.png" />,deleteData)
    }

    def subjectList() = {
        val subject = Subject.findAll
        "tbody" #> subject.map(s => {
                <tr><td>{s.full}</td><td>{s.short}</td></tr>
            })
    }

    def subjectForm() = {
        var short = ""
        var full = ""


        def deleteData() {
            val subjects = Subject.findAll("short"-> short)
            if(subjects.nonEmpty){
                subjects.head.delete
            }
        }

        def saveData() {
            val subjects = Subject.findAll("short"-> short)
            if(subjects.nonEmpty){
                val sub = subjects.head
                sub.full = full
                sub.short = short
                sub.save
            }
            else {
                val sub = Subject((new Date()).getTime, short, full)
                sub.save
            }
        }

        "#subjectFormName" #> SHtml.text(full, full = _,"class"->"name","maxlenght"->"30") &
        "#subjectFormShort" #> SHtml.text(short, short = _,"class"->"name","maxlenght"->"9") &
        "#subjectFormSubmit"  #> SHtml.button(<img src="/images/saveico.png" />,saveData,"onclick"->"return isValid(this);") &
        "#subjectFormDelete" #> SHtml.button(<img src="/images/delico.png" />,deleteData)

    }

    def dataAdmin() = {
        var email = ""
        var password = ""
        var admins = User.findAll(By(User.level,0))
        if(admins.nonEmpty) {
            val admin = admins.head
            email = admin.email.is
        }
        def saveData() {
            admins = User.findAll(By(User.level,0))
            if(admins.nonEmpty) {
                val admin = admins.head
                admin.email(email).password(password).save
            }
        }

        "#adminPassword" #> SHtml.text(password, password = _,"type"->"password","class"->"password","maxlenght"->"20") &
        "#adminEmail" #> SHtml.text(email, email = _,"class"->"required email","maxlenght"->"50","class"->"email") &
        "#adminSubmit" #> SHtml.button(<span>ZAPISZ! <img src="/images/saveico.png" /></span>, saveData,"onclick"->"return isValid(this);")

    }

}



