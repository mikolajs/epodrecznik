
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
        "#shortnews" #> news.map( item => {
                "h3" #> <h3>{item.title}</h3> &
                "span" #> <span>{item.date.toString}</span> &
                "p" #> <p>{item.content}</p> &
                "em" #> <a href={"/index?id=" + item._id}>Edytuj</a>
            })
    }
    
    def editNews() = {
        var id = S.param("id").openOr("0")
        var theNews = News.find(id).getOrElse(News.create)
        var title = theNews.title
        var content = theNews.content
        
        def deleteNews(){
            var news = News.find(id).getOrElse(News.create)
            if(id != "0") news.delete
            S.notice("Usunięto id=" + id)
        }
        
        def saveNews(){
            var news = News.find(id).getOrElse(News.create)
            if (title != "" && content != "") {
                val d = new Date()
                news._id = d.getTime.toString
                news.title = title 
                news.content = content
                if (id == "0") news.date = d.toString
                news.save
            }
        }
        
        "#editId" #> SHtml.text(id, id = _, "type"->"hidden") &
        "#editTitle" #> SHtml.text(title,title = _) &
        "#editContent" #> SHtml.textarea(content,content = _) &
        "#editDelete"#> SHtml.submit("Usuń",deleteNews) &
        "#editSave" #> SHtml.submit("Zapisz", saveNews)
        
    }
}
