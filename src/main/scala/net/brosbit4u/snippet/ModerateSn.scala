package net.brosbit4u.snippet


import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import _root_.net.brosbit4u.model._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import net.liftweb.http.js._
import Helpers._
import http.js.JsCmds._
import http.js.JE._
import _root_.net.brosbit4u.lib._

class ModerateSn {
  
	 def showSlides() = {
	   if(!isModerator) S.redirectTo("/user_mgt/login")
	   
	   val deleteUrlParam = S.param("del").openOr("n")
	   val confirmUrlParam = S.param("conf").openOr("n")
	   
	   if (deleteUrlParam != "n") deleteSlide(deleteUrlParam)
	   else if (confirmUrlParam != "n") confirmSlide(confirmUrlParam)
	   
	   val slides = Slide.findAll("confirmed"->false)
	   
	   "tbody" #> slides.map(
	       slide => <tr><td><a href={"/slideshow/"+slide._id.toString} target="_blank">{slide.title}</a></td>
	   	<td>{slide.subjectInfo}</td> <td>{slide.subjectLev.toString}</td>
	   	<td>{slide.departmentInfo}</td>
	   	<td>{if(slide.referTo.toString == "000000000000000000000000") <i>nowy</i> 
	   	  else <a href={"/slideshow/"+slide.referTo.toString} target="_blank">orginał</a>}</td>
	   	<td> {SHtml.a(Text("usuń"), Confirm("Na pewno usunąć bezpowrotnie wpis?",
	   	    RedirectTo("/moderate?del="+slide._id.toString)))}</td> 
    	<td> {SHtml.a(Text("zatwierdź"), Confirm("Na pewno zatwierdzić temat? Spowoduje to zastąpienie orginału.",
    	    RedirectTo("/moderate?conf="+slide._id.toString)))}</td></tr>
    	)
  }
	 
	 private def deleteSlide(slideId:String) { Slide.find(slideId) match {
	       case Some(slide) => if(!slide.public) slide.delete
	       case _ => 
	     }
	 }
	 
	 private def confirmSlide(slideId:String)  {
	    Slide.find(slideId) match {
	       case Some(slide) => {
	          val orginalPublicSlide = if(slide.referTo.getTime == 0L) slide else Slide.find(slide.referTo).getOrElse(slide)
	          orginalPublicSlide.moderator = User.currentUser.get.id.toString
	          orginalPublicSlide.referTo = new ObjectId("000000000000000000000000")
	          orginalPublicSlide.public = true 
	          orginalPublicSlide.save
	          
	          val lastAddedInDBList = LastAdded.findAll
	          val lastAdded = if(lastAddedInDBList.isEmpty) LastAdded.create else lastAddedInDBList.head
	          
	          val link = "/slideshow/" + orginalPublicSlide._id.toString
	          
	          val newLastAddedItem = LastAddedItem(slide.title,"p" ,link, Formater.formatDate(new Date()))
	          
	          if(lastAdded.content.exists(content => content.link == link )) 
	            lastAdded.content = lastAdded.content.map(content =>{
	              if (content.link == link) newLastAddedItem
	              else content
	            } )
	          else  lastAdded.content = newLastAddedItem::lastAdded.content
	          
	          if(lastAdded.content.length > 15) lastAdded.content = lastAdded.content.dropRight(1)
	          lastAdded.save
	          
	          if (orginalPublicSlide._id != slide._id) slide.delete
	       }
	       case _ =>
	     }
	 }
	 
	 private def isModerator():Boolean = {
	   User.currentUser match {
	     case Full(user) => if (user.level.is < 2) true else false
	     case _ => false
	   }
	 }
}
