
package net.brosbit4u.snippet

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{S,SHtml}
import mapper.{OrderBy,Descending}
import net.brosbit4u.model._
import Helpers._


class Main {
    
    def showLinks() = {
        val link = Link.findAll //OrderBy(News.date, Descending)
        "p" #> link.map( item => {
                <p><a href={item.link} target="_blank"><img src={item.imgPath} />
                <p><strong>{item.title}</strong><br/>{Unparsed(item.content)}</p> 
                </a></p>
            })
    }
    
   def showLatest() = {
     val lastAddedList = LastAdded.findAll
     val contentList = if(lastAddedList.isEmpty) Nil else lastAddedList.head.content
     "p" #> contentList.map(item => { 
       val p = item.what match {
       		case "p" => "presentation.png"
       		case "b" => "book.png"
       		case _ => "nic"
       	}
       <p><a href={item.link}>
       <img alt="x" src={"images/" + p} /><span>{item.date}</span> 
       <strong>{item.info}</strong> 
       </a></p>
     } )
   }
    
   def logInfo() = {
     var isLoged = false
     val currentUserName = User.currentUser match {
       case Full(user) => isLoged = true; user.fullName
       case _ => " Zaloguj"
     }
        if (isLoged) "#logInfo" #> <a title="WYLOGUJ!" href="/user_mgt/logout"><img src="/images/loged.png"/>{ currentUserName }</a>
        else "#logInfo" #> <a href="/user_mgt/login"><img src="/images/nologed.png"/> { currentUserName }</a>
      }
    
   
}
