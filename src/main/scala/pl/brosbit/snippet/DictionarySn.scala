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
import net.liftweb.common.Empty

class DictionarySn {
    
    def searchEntry() = {
        var word = ""
        
        def search(){
        	S.redirectTo("/dictionary?w="+word)
        }
                 
       "#entry" #> SHtml.text(word, x => word = x.trim) &
       "#search" #>  SHtml.button(<img src="/images/searchico.png"/>, search,"title"->"Szukaj") 
       
    }
    def searchResult() = {
        val levels = Map((1->"łatwy"),(2->"średni"),(3->"wyższy"))
        val word = S.param("w").getOrElse("")
        "ul *" #> Dictionary.findAll(("headword"->word)).map(dict => {
           <li> <a href={"/headword/" + dict._id.toString} target="_blanck"> 
            			{dict.headword} <span> - {dict.subjectName}, poziom: {levels(dict.level)}</span></a></li>
        })              
    }
    
    def autocomplete() = {
        val entries = Dictionary.findAll.map(dict => "'" + dict.headword + "'")
        "script" #> <script>{"var availableTags=[" + entries.mkString(", ") +"];"}</script>
    }

}