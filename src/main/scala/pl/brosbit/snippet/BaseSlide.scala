package pl.brosbit.snippet

import pl.brosbit._
import model._
import _root_.net.liftweb._
import common._
import util._

class BaseSlide {

  def isOwner(authors:List[Edit]) = {
    User.currentUser match {
      case Full(user) => {
        if(authors.isEmpty) true
        else authors.head.user == user.id.toString
      }
      case _ => false
    }
  }
  
  def departmentSelect() = {
    val depOptions = Department.findAll.map(d => 
        	<option class={d.subject.toString} value={d._id.toString}>{d.name}</option>)
    <select id="departments">{depOptions}</select>
  }
  
}