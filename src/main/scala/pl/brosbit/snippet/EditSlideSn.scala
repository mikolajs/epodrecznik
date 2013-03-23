package pl.brosbit.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class EditSlideSn extends BaseSlide with RoleChecker {
  
 val id = S.param("id").openOr("0")
 var slide = if (id != "0") Slide.find(id).getOrElse(Slide.create) else Slide.create
 var slideCont = SlideContent.find(slide.slides).getOrElse(SlideContent.create)
 if(slide.slides != slideCont._id) slide.slides = slideCont._id

 //for showslides - viewer 
  def slideData() = {
      "#title" #> <span>{slide.title}</span> &
      "#subject" #> <span>{slide.subjectInfo}</span> &
      "#etap" #> <span>{slide.subjectLev.toString}</span> &
      "#department" #> <span>{slide.departmentInfo}</span> &
      "#slideHTML" #>  Unparsed(slideCont.slides)  
  }
  
  
  //edit slides 
  def formEdit() = {
    
    var ID = if(id == "0") "0" else slide._id.toString
    var title = slide.title
    var subjectId = if(slide.subjectId != null) slide.subjectId.toString else ""
    var subjectLev = slide.subjectLev.toString
    var departmentId = if(slide.departmentId != null) slide.departmentId.toString else ""
    var departmentInfo = slide.departmentInfo
    var public = if(slide.public) "TAK" else "NIE"
    
    var slidesData = slideCont.slides
    //println("------------slides data -----------------\n" +slidesData)
    
    val listSubject = Subject.findAll.map(s => {(s._id.toString ,s.full)})
    
    //poprawić - uwzględnić fak, że new Slide już istnieje - chyba, że potrzebujemy kopi
    def saveData() {
      val userId = User.currentUser.open_!.id.is
      if(slide.authorId == 0L || slide.authorId == userId) {
          val slidesContentHtml = Unparsed(slidesData)
           slide.title = title
           slide.subjectId = new ObjectId(subjectId)
          val sub = Subject.find(new ObjectId(subjectId)).getOrElse(Subject.create);
          slide.subjectInfo = sub.full
          slide.subjectLev = subjectLev.toInt
          slide.departmentId = new ObjectId(departmentId)
          slide.departmentInfo = Department.find(new ObjectId(departmentId)).
              getOrElse(Department.create(new ObjectId(subjectId))).name
          slide.authorId = userId
          slide.public = public == "TAK"
          slideCont.slides = slidesContentHtml.toString
          slideCont.save
          slide.slides = slideCont._id      
          slide.save 
      }
       
      
      S.redirectTo("/resources/slides") //!important must refresh page
    }
    
    def deleteData() {
      val userId = User.currentUser.open_!.id.is
     if (id != "0") Slide.find(id) match {
         case Some(slide) if slide.authorId == userId => {
           slideCont.delete
    	   slide.delete
        } 
         case _ =>
      }
      
      S.redirectTo("/resources/slides")
    }
      
    def cancelAction() {
      S.redirectTo("/resources/slides")
    }
    
    val levList = List(("1","I"),("2","II"),("3","III"),("4","IV"),("5","V"))
    val publicList = List(("TAK","TAK"),("NIE","NIE"))
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#titleTheme" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjects" #> SHtml.select(listSubject, Full(subjectId),subjectId = _) &
    "#subjectLevel" #> SHtml.select(levList,Full(subjectLev),subjectLev = _) &
    "#public" #> SHtml.select(publicList,Full(public), public = _) &
    "#departmentHidden" #> SHtml.text(departmentId, departmentId = _, "type"->"hidden") &
    "#departments" #> departmentSelect() &
    "#slidesData" #> SHtml.text(slidesData, slidesData = _, "type"->"hidden") &
    "#save" #> SHtml.button(<img src="/images/saveico.png"/>, saveData,"title"->"Zapisz") &
    "#delete" #> (if(isModerator) SHtml.button(<img src="/images/delico.png"/>, 
        deleteData,"title"->"Usuń") else <span></span>) &
    "#cancel" #> SHtml.button(<img src="/images/cancelico.png"/>, cancelAction,"title"->"Anuluj") 
  }
  
  def subjectList() = {
    val subjects = Subject.findAll
    "#subjectList" #> subjects.map(subject => "a" #> <a href="#" onclick="selectSubject(this)">{subject.full} </a>)
  } 
  
}
