package pl.brosbit.snippet


import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import net.liftweb.http.js._
import Helpers._
import http.js.JsCmds._
import http.js.JE._
import pl.brosbit.lib._
import pl.brosbit.model._

class ModerateSn extends RoleChecker {
  
	 def showSlides() = {
	   if(!isModerator) S.redirectTo("/user_mgt/login")
	   

	   val confirmUrlParam = S.param("conf").openOr("n")
	   
	   if (confirmUrlParam != "n") confirmSlide(confirmUrlParam)
	   
	   val slides = Slide.findAll("public"->false)
	   
	   "tbody" #> slides.map(
	       slide => <tr><td><a href={"/slide/"+slide._id.toString} target="_blank">{slide.title}</a></td>
	   	<td>{slide.subjectInfo}</td> <td>{slide.subjectLev.toString}</td>
	   	<td>{slide.departmentInfo}</td>
	   	<td>{if(slide.referTo.toString == "000000000000000000000000") <i>nowy</i> 
	   	  else <a href={"/slide/"+slide.referTo.toString} target="_blank">orginał</a>}</td>
    	<td> {SHtml.a(Text("zatwierdź"), Confirm("Na pewno zatwierdzić temat? Spowoduje to zastąpienie orginału.",
    	    RedirectTo("/moderate?conf="+slide._id.toString)))}</td></tr>
    	)
  }
	
	 private def confirmSlide(slideId:String)  {
	    Slide.find(slideId) match {
	       case Some(slide) => {
	          //look for original public slide if is first slide orginal is itself
	          if(slide.referTo.getTime == 0L) setAndSaveNewSlide(slide)
	          else {
	               Slide.find(slide.referTo) match {
	                     case Some(publicSlide) => alterPublicSlideAndDeletePrivate(publicSlide, slide)
	                     case _ => setAndSaveNewSlide(slide)
	               }
	          }  
	       }
	       case _ =>
	     }
	 }
	 
	 private def setAndSaveNewSlide(slide:Slide){
	      slide.public = true
	      slide.moderator = User.currentUser.get.id.toString
	      slide.save
	      appendLastAdded(slide)
	 }
	 
	 private def appendLastAdded(slide:Slide){
		 val lastAddedInDBList = LastAdded.findAll
		 val lastAdded = if(lastAddedInDBList.isEmpty) LastAdded.create else lastAddedInDBList.head

		 val link = "/slide/" + slide._id.toString

		if(lastAdded.content.length > 15) lastAdded.content = lastAdded.content.dropRight(1)
		lastAdded.save
	 }

    private def alterPublicSlideAndDeletePrivate(publicSlide: Slide, privateSlide: Slide) {

        publicSlide.moderator = User.currentUser.get.id.toString
        SlideContent.find(publicSlide.slides) match {
            case Some(slideCont) => slideCont.delete
            case _ =>
        }
        publicSlide.referTo = new ObjectId("000000000000000000000000")
        publicSlide.public = true
        publicSlide.title = privateSlide.title
        publicSlide.departmentId = privateSlide.departmentId
        publicSlide.departmentInfo = privateSlide.departmentInfo
        publicSlide.subjectId = privateSlide.subjectId
        publicSlide.subjectInfo = privateSlide.subjectInfo
        publicSlide.subjectLev = privateSlide.subjectLev
        publicSlide.authors = privateSlide.authors
        publicSlide.slides = privateSlide.slides

        privateSlide.delete
        publicSlide.save
    }
	 
}
