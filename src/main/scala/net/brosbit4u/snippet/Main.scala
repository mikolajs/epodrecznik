
package net.brosbit4u.snippet

import java.util.Date
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{S,SHtml}
import mapper.{OrderBy,Descending}
import net.brosbit4u.model._
import Helpers._


class Main {
    
    def shortNews() = {
        val news = News.findAll //OrderBy(News.date, Descending)
        ".boxStyle" #> news.map( item => {
                "span" #> <span>{item.date.toString}</span> &
                "p" #> <p><strong>{item.title}</strong><br/>{item.content}</p> &
                "em" #> <a href={"/index?id=" + item._id}>Czytaj dalej...</a>
            })
    }
    
   
}
