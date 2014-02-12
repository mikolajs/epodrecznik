package pl.brosbit.snippet

import java.util.Date
import scala.xml.{ Text, XML, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
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

class ShowLessonSn  {

    //for showslides - viewer 
    def showAsDocument() = {
        val infoError = "section" #> <section><h1>Nie znaleziono slajdu</h1></section>
        val id = S.param("id").openOr("0")
        if (id != "0") {
            Slide.find(id) match {
                case Some(slide) => {
                    SlideContent.find(slide.slides) match {
                        case Some(slideCont) => {
                            "#title" #> <span>{ slide.title.take(30) }</span> &
                            "#course-info" #> <span>{ slide.subjectLev.toString }</span> &
                            "section" #> Unparsed(slideCont.details)  
                        }
                        case _ => infoError
                    }
                }
                case _ => infoError
            }
        }
        else infoError
    }
    
    def showAsPresentation() = {
        
    }
}
