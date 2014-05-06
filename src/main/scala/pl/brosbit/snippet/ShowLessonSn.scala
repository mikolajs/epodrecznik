package pl.brosbit.snippet

import java.util.Date
import java.util.Random
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
	
    val rand = new Random
    //for showslides - viewer 
    def showAsDocument() = {
        val infoError = "section" #> <section><h1>Nie znaleziono slajdu</h1></section>
        val id = S.param("id").openOr("0")
        if (id != "0") {
            Lesson.find(id) match {
                case Some(lesson) => {                
                       val courseInfo = Course.find(lesson.courseId) match {
                           case Some(course) => course.getInfo
                           case _ => "Nieznany kurs"
                       }
               
                       val (headWords, restOf) = lesson.contents.partition(x => x.what == "word")
                       val(quests, restOf2) = restOf.partition(x => x.what == "quest")
                       val(docum, videos) = restOf2.partition(x => x.what =="doc")
                       val listHWid = headWords.map(hw => hw.link.drop(3))
                       val listQid = quests.map(q => q.link.drop(3))
                       val listVideos = videos.map(v =>v.link.drop(3))
                       val listDocs = docum.map(d => d.link.drop(3))
                      val vs = Video.findAll(("_id"  -> ("$in" -> listVideos)))
                      val hws = HeadWord.findAll(("_id"  -> ("$in" -> listHWid)))
                      val qts = QuizQuestion.findAll(("_id"  -> ("$in" -> listQid)))
                      val docs = Document.findAll(("_id"  -> ("$in" -> listDocs)))
                      
                      val content =  lesson.contents.map(item => item.what match {
                          case "word" =>  {
                             val headW = hws.find(i => i._id.toString ==  item.link.drop(3)).
                             	getOrElse(HeadWord.create)
                             	"<section class=\" headword\"/><h2>" + headW.title + "</h2>" + headW.content + "</section>"
                             	
                          }
                          case "quest" =>  {
                              createQuest(qts.find(q => q._id.toString == item.link.drop(3)).
                              	getOrElse(QuizQuestion.create))
                          }
                          case "video" => {
                              vs.find(v =>v._id.toString() == item.link.drop(3)) match {
                                  case Some(video) => {
                                     <iframe width="853" height="480"  src={"//www.youtube.com/embed/"+video.link}  frameborder="0"  allowfullscreen=""></iframe>
                                  }
                                  case _ => <h4>Błąd - nie ma takiego filmu</h4>
                              }
                          }
                          case "doc" => {
                              val docModel = docs.find(i => i._id.toString == item.link.drop(3)).getOrElse(Document.create)
                             "<section class=\"document\"> <h3>" + docModel.title + "</h3>\n " + docModel.content + "</section>"
                          }
                          case _ => <h4>Błąd - nie ma takiego typu zawartości</h4>
                      }).mkString("\n")
                      		"#run-as-slides [href]" #> ("/lesson-slides/" +lesson._id.toString()) &
                            "#title" #> <h1>{ lesson.title }</h1> &
                            "#course-info" #> <span>{ courseInfo}</span> &
                            "#sections" #> Unparsed(content)  &
                            "#extra-info *" #> Unparsed(lesson.extraText)            
                }
                case _ => infoError
            }
        }
        else infoError
    }
    
    private def createQuest(quest:QuizQuestion) = {
        val witch = rand.nextInt(quest.fake.length+1)
        val all = quest.fake.take(witch) ++ (quest.answer::quest.fake.drop(witch))
        var n = -1
        var qId = quest._id.toString
       <section class="question">
        <h4>Zadanie</h4>
        <input type="hidden" class="correct"  value={witch.toString}/>
    	<div class="questionText">{Unparsed(quest.question)} </div>
    	<ul>	{ all.map( a => { 
    	    		n += 1
        		    <li><input type="radio"  name={quest._id.toString} class={"answer" + n} /> <label>{a}</label></li>
        		    })
        		} </ul>
    	<button onclick="checkAnswer(this)">Sprawdź</button>
    	<p class="alertWell"></p>
    	</section>
    }
    
    def slideData = {
        
    }
    
}
