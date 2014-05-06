package pl.brosbit.snippet

import pl.brosbit._
import model._
import _root_.net.liftweb._
import common._
import util._
import Helpers._

class BaseSnippet {

  
  def departmentSelect() = {
    val depOptions = Department.findAll.map(d => 
        	<option class={d.subject.toString} value={d._id.toString}>{d.name}</option>)
    <select id="departments">{depOptions}</select>
  }
   val levList = List(("1","podstawowy"),("2","średni"),("3","rozszerzony"))
   
   def subjectSelect() = {
    val subj = "Wszystkie"::Subject.findAll.map(s => s.full)
    "#subjectSelect" #> subj.map(s => 
        "option" #> <option value={s}>{s}</option>)
  }
   
     
  
}