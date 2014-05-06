package pl.brosbit.snippet



import java.io.ByteArrayInputStream
import scala.xml.{ Unparsed}
import _root_.net.liftweb._
import http.{ S, SHtml, FileParamHolder}
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import mongodb._
import com.mongodb.gridfs._

class VideoSn  extends BaseSnippet {
	def showVideos = {
	    val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
	    
	    val idToDel = S.param("del").openOr("")
	    if(idToDel.length > 20){
	        Video.find(idToDel) match {
	            case Some(video) if(video.authorId == user.id.is) =>  {
	                video.delete
	            }
	            case _ => 
	        }
	    }
	    
	    
	     "tr" #> Video.findAll("authorId" -> user.id.is).map(video => {
	        <tr><td><a href={"http://youtube.com/embed/" + video.link} target="_blank">{video.title}</a></td>
	        <td>{video.subject}</td><td>{video.department}</td><td>{video.descript}</td>
	        <td><a href={"/resources/video?del=" + video._id.toString}>Usuń</a></td></tr>
	    })
	}
	
	
	def add = {
	
	    var title = ""
	    var descript = ""
	    var subjectId = ""
	    var departmentId = "000000000000000000000000"
	    var link = ""
	    

        def save():Unit = {
            val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")

            val sub = Subject.find(subjectId).getOrElse(Subject.create)
                
                if(sub.full == "") return
                val dep = Department.find(departmentId).getOrElse(Department.create(sub._id))
                
                if(dep.name == "") return

                val video = Video.create(link , sub._id, dep._id, user.id.is)
                video.department = dep.name
                video.subject = sub.full
                video.title = title
                video.descript = descript
                video.save

        }
	    
	        
	    val subjects = Subject.findAll.map(sub => (sub._id.toString, sub.full))
	   
	        
	    "#title" #> SHtml.text(title, x => title = x.trim) &
	    "#subjects" #> SHtml.select(subjects, Full(subjects.head._1), subjectId = _) &
	    "#departmentHidden" #> SHtml.text(departmentId, departmentId = _) &
	    "#departments" #> departmentSelect() &
	    "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
	    "#link" #> SHtml.text(link, x => link = x.trim) &
	    "#save" #> SHtml.submit("Dodaj", save)
	    
	}
}