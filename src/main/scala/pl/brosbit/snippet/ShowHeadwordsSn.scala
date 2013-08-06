package pl.brosbit.snippet

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import Helpers._
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId

class ShowHeadwordsSn {
     val subjectId = S.param("w").openOr("0")
     val subject = Subject.find(subjectId).getOrElse({
         	val subList = Subject.findAll
         	if(subList.isEmpty) Subject.create
         	else subList.head
         })
     
      def showWords() = {
         
          "li" #> Dictionary.findAll("subject"-> subject._id.toString).map(dict =>
              <li><a href={"/headword?w="+ dict._id.toString} target="_blanck">{dict.headword}</a> 
              <a href={"/resources/editheadword/"+dict._id.toString}><img src="/images/editico.png" /></a></li>)
      }
      
      def showSubjects = {
          val subjects = Subject.findAll.map(s => (s._id.toString, s.full))
          "#choiseSubject" #> SHtml.select(subjects, Full(subject._id.toString), x => x)
      }
}