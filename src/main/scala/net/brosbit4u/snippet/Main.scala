
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
        ".img-box" #> link.map( item => {
                "img" #> <img src={item.imgPath} /> &
                "p" #> <p><strong>{item.title}</strong><br/>{Unparsed(item.content)}</p> &
                "em" #> <a href={item.link} target="_blanck">Idź do zasobów</a>
            })
    }
    
   def showLatest() = {
     val newContentsList = NewContent.findAll
     val contentList = if(newContentsList.isEmpty) Nil else newContentsList.head.content
     "p" #> contentList.map(item => {     
       "span" #> <span>{item.date}</span> & 
       "strong" #> <strong>{item.info}</strong> &
       "em" #> <em>{item.what}</em>
       "a" #>  <a href={item.link}><img alt="-->" src="images/arrow1.gif" /> </a>
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
