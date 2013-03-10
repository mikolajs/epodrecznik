
package pl.brosbit.snippet

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{S,SHtml}
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import Helpers._


class Main {
    
    def showLinks() = {
        val link = Link.findAll //OrderBy(News.date, Descending)
        ".img-box" #> link.map( item => {
                <div class="img-box"><p><a href={item.link} target="_blank"><img src={item.imgPath} />
                <strong>{item.title}</strong><br/>{Unparsed(item.content)}
                </a></p></div>
            })
    }
    
   def showLatest() = {
     val lastAddedList = LastAdded.findAll
     val contentList = if(lastAddedList.isEmpty) Nil else lastAddedList.head.content
     "p" #> contentList.map(item => { 
       <p><a href={item.link}>
       <img alt="lekcja" src="/images/lekcja.png" /><span>{item.date}</span> 
       <strong>{item.info}</strong> 
       <span> - {item.subject}</span>
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
