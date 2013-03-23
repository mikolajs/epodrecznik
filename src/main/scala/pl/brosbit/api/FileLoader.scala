package pl.brosbit.api

import net.liftweb._
import common._
import util._
import http._
import com.mongodb.gridfs._
import net.liftweb.mongodb._
import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, File }
import provider.servlet.HTTPServletContext
import com.mongodb.DBObject

object FileLoader {
  
  def file(id: String): Box[LiftResponse] = {
    var outputStream = new ByteArrayOutputStream()
    var mime = ""
    var fileName = ""
  MongoDB.use(DefaultMongoIdentifier) { db =>
  	val fs = new GridFS(db)
  	val foundFile = fs.findOne(new org.bson.types.ObjectId(id))
  	if (foundFile == null) println ("++!!!!!!!!!! imageLoader file is null " + id +" !!!!!!!!!!!") 
  	else {
  	  foundFile.writeTo(outputStream)
  	  mime = foundFile.getFilename().split('.').last
  	  fileName = foundFile.getFilename()
  	}
  }
    val inputStream = new ByteArrayInputStream(outputStream.toByteArray)
    if(inputStream.available() < 10){
      Full(NotFoundResponse("Not found"))
    } 
    else {
      Full(StreamingResponse(inputStream, () => (), inputStream.available().toLong,
    ("Content-Type", "image/"+mime) :: ("Content-Disposition", "attachment; filename=\"" +fileName + "\"" ) :: Nil,
    Nil, 200))
    		  
    }
  }

 
 
 
}