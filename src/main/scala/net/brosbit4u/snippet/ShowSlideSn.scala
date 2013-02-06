package net.brosbit4u.snippet

import java.util.Date
import scala.xml.{ Text, XML, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import _root_.net.brosbit4u.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class ShowSlideSn extends {

    //for showslides - viewer 
    def slideData() = {
        val infoError = "#slideHTML" #> <section class="slide" id="slide-1"><h1>Nie znaleziono slajdu</h1></section>
        val id = S.param("id").openOr("0")
        if (id != "0") {
            Slide.find(id) match {
                case Some(slide) => {
                    SlideContent.find(slide.slides) match {
                        case Some(slideCont) => {
                            "#title" #> <span>{ slide.title.take(30) }</span> &
                                "#subject" #> <span>{ slide.subjectInfo }</span> &
                                "#etap" #> <span>{ slide.subjectLev.toString }</span> &
                                "#department" #> <span>{ slide.departmentInfo }</span> &
                                "#slideHTML" #> Unparsed(slideCont.slides)
                        }
                        case _ => infoError
                    }
                }
                case _ => infoError
            }
        }
        else infoError

    }
}
