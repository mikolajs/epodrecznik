package net.brosbit4u.snippet


import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import net.brosbit4u.model._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import net.liftweb.http.js._
import Helpers._
import http.js.JsCmds._
import http.js.JE._
import net.liftweb.util.ToJsCmd


class ModerateSn {
	 def showThemes() = {
	   if(!isModerator) S.redirectTo("/user_mgt/login")
	   val del = S.param("del").openOr("n")
	   val conf = S.param("conf").openOr("n")
	   if (del != "n") {
	     Theme.find(del) match {
	       case Some(theme) => if(!theme.confirmed)theme.delete
	       case _ => 
	     }
	   }
	   else if (conf != "n") {
	     Theme.find(conf) match {
	       case Some(theme) => {
	          val th = Theme.find(theme.referTo).getOrElse(theme)
	          th.moderator = User.currentUser.get.id.toString
	          th.referTo = "0"
	          th.confirmed = true 
	          th.save
	          if (th._id != theme._id) theme.delete
	       }
	       case _ =>
	     }
	   }
	   
	   val themes = Theme.findAll("confirmed"->false)
	   "tbody" #> themes.map(theme => <tr><td><a href={"/slideshow/"+theme._id.toString} target="_blank">{theme.title}</a></td>
	   	<td>{theme.subject}</td> 
	   	<td>{if(theme.referTo.toString() == "0") <i>nowy</i> else <a href={"/slideshow/"+theme.referTo.toString} target="_blank">orginał</a>}</td>
	   	<td> {SHtml.a(Text("usuń"), Confirm("Na pewno usunąć bezpowrotnie wpis?",RedirectTo("/moderate?del="+theme._id.toString)))}</td> 
    	<td> {SHtml.a(Text("zatwierdź"), Confirm("Na pewno zatwierdzić temat? Spowoduje to zatąpienie orginału.",RedirectTo("/moderate?conf="+theme._id.toString)))}</td></tr>)
  }
	 
	 
	 def isModerator():Boolean = {
	   User.currentUser match {
	     case Full(user) => if (user.level.is < 2) true else false
	     case _ => false
	   }
	 }
}