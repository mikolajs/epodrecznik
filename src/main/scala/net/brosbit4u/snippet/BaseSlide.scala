package net.brosbit4u.snippet

import _root_.net.brosbit4u._
import model._
import _root_.net.liftweb._
import common._
import util._

class BaseSlide {

  def canEdit:Boolean = {
    User.currentUser match {
        case Full(user) => {
          if(user.level.is < 7) true 
          else false
        }
        case _ => {
          false
        }
      } 
  }
  
  def isModerator = User.currentUser match {
    case Full(user) => if (user.level.is < 2) true else false
    case _ => false
  }
  
  def isAdmin = User.currentUser match {
    case Full(user) => user.level.is == 0
    case _ => false
  }
}