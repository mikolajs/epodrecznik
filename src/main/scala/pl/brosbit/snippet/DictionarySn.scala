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
        var entry = ""
        //var allSubjects = Subject.findAll.map(sub => (sub.full,sub.full))
        
        def search(){
        	S.redirectTo("/dictionary?e="+entry)
        }
        
           
       "#entry" #> SHtml.text(entry, x => entry = x.trim) &
       "#search" #>  SHtml.button(<img src="/images/searchico.png"/>, search,"title"->"Szukaj") 
       
    }
    
    def searchResult() = {
        val entry = S.param("e").getOrElse("")
        "ul" #> Dictionary.findAll(("entry"->entry)).map(dict => {
            "li *" #> <a href={"/dictionary?e=" + entry + "&s=" + dict._id.toString}>{dict.entry} 
            			<span> - [{dict.subjectName}]</span></a>
        })              
    }
    
    def showEntry() = {
        val id = S.param("s").getOrElse("")
        Dictionary.find(id) match {
            case Some(dict) => {
                "#showEntry" #> <section><h2>{dict.entry}</h2>{Unparsed(dict.content)}</section>
            }
            case _ => "#showEntry" #> <span></span>
        }
    }
    
    def autocomplete() = {
        val entries = Dictionary.findAll.map(dict => "'" + dict.entry + "'")
        "script" #> <script>{"var availableTags=[" + entries.mkString(", ") +"];"}</script>
    }

}