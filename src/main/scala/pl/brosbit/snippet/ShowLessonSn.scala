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
                       val (headWords, quests) = lesson.contents.partition(x => x.what == "h")
                       val listHWid = headWords.map(hw => hw.link.drop(3))
                       val listQid = quests.map(q => q.link.drop(3))
                      val hws = HeadWord.findAll(("_id"  -> ("$in" -> listHWid)))
                      val qts = QuizQuestion.findAll(("_id"  -> ("$in" -> listQid)))
                      val content =  lesson.contents.map(item => {
                          if(item.what == "h") {
                             hws.find(i => i._id.toString ==  item.link.drop(3)).
                             	getOrElse(HeadWord.create).content            
                          }
                          else {
                              createQuest(qts.find(q => q._id.toString == item.link.drop(3)).
                              	getOrElse(QuizQuestion.create))
                          }
                      }).mkString("\n")
                            "#title" #> <span>{ lesson.title }</span> &
                            "#course-info" #> <span>{ courseInfo}</span> &
                            "#sections" #> Unparsed(content)  &
                            "#extra-info" #> Unparsed(lesson.extraText)
                   
                }
                case _ => infoError
            }
        }
        else infoError
    }
    
    private def createQuest(quest:QuizQuestion) = {
        val witch = rand.nextInt(quest.fake.length+1)
        val all = quest.fake.take(witch) ++ (quest.answer::quest.fake.drop(witch))
        var n = 0
       <section class="question">
        <input type="hidden" class="correct"  value={witch.toString}/>
    	<div class="questionText">{quest.question} </div>
    	<div class="answerList"><ul>
        		{	n += 1
        		    all.map( a => 
        		    <li><label>{a}</label><input type="radio"  class={"answer" + n}/></li>)
        		}
    		</ul>
    	</div>
    	</section>
    }
}
