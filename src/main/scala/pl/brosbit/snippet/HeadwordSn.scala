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

class HeadwordSn {

    def showHeadword() = {
        val infoError = "#slideHTML" #> <section class="slide" id="slide-1"><h1>Nie znaleziono slajdu</h1></section>
        val id = S.param("id").openOr("0")
        val levels = Map((1->"łatwy"),(2->"średni"),(3->"wyższy"))
        if (id != "0") {
            Dictionary.find(id) match {
                case Some(dict) => {
                    "#word" #> <span>{ dict.headword }</span> &
                    "#subject" #> <span>{ dict.subjectName}</span> &
                    "#level" #> <span>{ levels(dict.level) }</span> &
                    "#slideHTML" #> Unparsed(dict.content)
                }

                case _ => infoError
            }
        } else infoError

    }
}