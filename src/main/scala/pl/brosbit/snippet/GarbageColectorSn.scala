package pl.brosbit.snippet

import _root_.net.liftweb._
import util.Helpers._
import http._
import pl.brosbit.model._
import pl.brosbit.lib._

object GarbageColectorSn {
    
    def calculate() = {
        def search() {
            
        }
        "#calculate" #> SHtml.submit("Wyszukaj", search)
    }
    
    def show() = {
        
        
        "li" #> "" &
        "span" #> 23
    }
    
    
    def delete() = {
        def mkDelete(){
            
        }
        "#delete" #> SHtml.submit("Kasuj!", mkDelete)  
    }
    
    def test() = {
        val gcFiles = new GCFiles
        val all = gcFiles.getAllInGridFS()
        "li" #> all.map(x => <li><img src={x} /></li>)
    }

}