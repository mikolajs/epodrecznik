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
	 def showThemes() = {
	   if(!isModerator) S.redirectTo("/user_mgt/login")
	   val del = S.param("del").openOr("n")
	   val conf = S.param("conf").openOr("n")
	   if (del != "n") {
	     Slide.find(del) match {
	       case Some(slide) => if(!slide.confirmed)slide.delete
	       case _ => 
	     }
	   }
	   else if (conf != "n") {
	     Slide.find(conf) match {
	       case Some(slide) => {
	         //get original slide if exist, if is new use it
	          val th = if(slide.referTo.getTime == 0L) slide else Slide.find(slide.referTo).getOrElse(slide)
	          th.moderator = User.currentUser.get.id.toString
	          th.referTo = new ObjectId("000000000000000000000000")
	          th.confirmed = true 
	          th.save
	          //add to database new content to show in index.html
	          val newContList = NewContent.findAll
	          val newCont = if(newContList.isEmpty) NewContent.create else newContList.head
	          val link = "/slideshow/" + th._id.toString
	          val cont = AddedContent(slide.title,"p" ,link, Formater.formatDate(new Date()))
	          if(newCont.content.exists(c => c.link == link )) 
	            newCont.content = newCont.content.map(c =>{
	              if (c.link == link) cont
	              else c
	            } )
	          else  newCont.content = cont::newCont.content
	          if(newCont.content.length > 15) newCont.content = newCont.content.dropRight(1)
	          newCont.save
	          //delete copy if it's not new slide
	          if (th._id != slide._id) slide.delete
	       }
	       case _ =>
	     }
	   }
	   
	   val themes = Slide.findAll("confirmed"->false)
	   "tbody" #> themes.map(slide => <tr><td><a href={"/slideshow/"+slide._id.toString} target="_blank">{slide.title}</a></td>
	   	<td>{slide.subjectInfo}</td> <td>{slide.subjectLev.toString}</td><td>{slide.departmentInfo}</td>
	   	<td>{if(slide.referTo.toString == "000000000000000000000000") <i>nowy</i> else <a href={"/slideshow/"+slide.referTo.toString} target="_blank">orginał</a>}</td>
	   	<td> {SHtml.a(Text("usuń"), Confirm("Na pewno usunąć bezpowrotnie wpis?",RedirectTo("/moderate?del="+slide._id.toString)))}</td> 
    	<td> {SHtml.a(Text("zatwierdź"), Confirm("Na pewno zatwierdzić temat? Spowoduje to zastąpienie orginału.",RedirectTo("/moderate?conf="+slide._id.toString)))}</td></tr>)
  }
	 
	 
	 def isModerator():Boolean = {
	   User.currentUser match {
	     case Full(user) => if (user.level.is < 2) true else false
	     case _ => false
	   }
	 }
}
