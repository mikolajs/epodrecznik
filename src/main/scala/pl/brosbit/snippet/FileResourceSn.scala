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

class FileResourceSn extends BaseSnippet {
	def showMyFiles = {
	    val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
	    
	    val idToDel = S.param("del").openOr("")
	    if(idToDel.length > 20){
	        FileResource.find(idToDel) match {
	            case Some(file) if(file.authorId == user.id.is) =>  {
	                 MongoDB.use(DefaultMongoIdentifier) { db =>
	                 val fs = new GridFS(db)
	                 fs.remove(file.fileId)
	                 }
	                file.delete
	            }
	            case _ => 
	        }
	    }
	    
	    
	     "tr" #> FileResource.findAll("authorId" -> user.id.is).map(file => {
	        <tr><td><a href={"/file/" + file.fileId.toString}>{file.title}</a></td>
	        <td>{file.subject}</td><td>{file.department}</td><td>{file.descript}</td>
	        <td><a href={"/resources/files?del=" + file._id.toString}>Usuń</a></td></tr>
	    })
	}
	
	
	def add = {
	
	    var title = ""
	    var descript = ""
	    var subjectId = ""
	    var departmentId = "000000000000000000000000"
	    var mimeType = ""
	    var extension = ""
	    var fileHold: Box[FileParamHolder] = Empty
	    
	    def isCorrect = fileHold match {
          case Full(FileParamHolder(_, mime, _, data))  => {
            mimeType = mime.toString
            true
          }
          case Full(_) => {
            S.error("Nieprawidłowy format pliku!")
            false
          }
          case _ => {
            S.error("Brak pliku?")
            false
          }
        }

        def save():Unit = {
            val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
          
            if (isCorrect) {
                val sub = Subject.find(subjectId).getOrElse(Subject.create)
                
                if(sub.full == "") return
                val dep = Department.find(departmentId).getOrElse(Department.create(sub._id))
                
                if(dep.name == "") return
                var fileId = "0"
                MongoDB.use(DefaultMongoIdentifier) { db =>
                    val fs = new GridFS(db)
                    val inputFile = fs.createFile(new ByteArrayInputStream(fileHold.get.file))
                    inputFile.setContentType(mimeType)
                    inputFile.setFilename(fileHold.get.fileName)
                    inputFile.save
                    fileId = inputFile.getId().toString()
                }
               
                 if(fileId == "0") return
                val fileRes = FileResource.create(new ObjectId(fileId) , sub._id, dep._id, user.id.is)
                fileRes.department = dep.name
                fileRes.subject = sub.full
                fileRes.title = title
                fileRes.descript = descript
                fileRes.save
                
            }

        }
	    
	        
	    val subjects = Subject.findAll.map(sub => (sub._id.toString, sub.full))
	   
	        
	    "#title" #> SHtml.text(title, x => title = x.trim) &
	    "#subjects" #> SHtml.select(subjects, Full(subjects.head._1), subjectId = _) &
	    "#departmentHidden" #> SHtml.text(departmentId, departmentId = _) &
	    "#departments" #> departmentSelect() &
	    "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
	    "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
	    "#save" #> SHtml.submit("Dodaj", save)
	    
	}
}