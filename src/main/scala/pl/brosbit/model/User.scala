
package pl.brosbit.model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.sitemap.Loc._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="admin" at="loginBind">
			       <lift:bind /></lift:surround>)			       
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password, textArea)

  // comment this line out to require email validations
  //override def skipEmailValidation = false
  override def createUserMenuLoc = Empty
  override def editUserMenuLoc= Empty
//  override def loginFirst = If(
//    loggedIn_? _,
//    () => {
//      import net.liftweb.http.{RedirectWithState, RedirectState}
//      val uri = Full("/editable")
//      RedirectWithState(
//        loginPageURL,
//        RedirectState( ()=>{loginRedirect.set(uri)})
//      )
//    }
//  )
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server
  object role extends MappedString(this,1)
  //object karma extends MappedLong(this)
  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
  def fullName = " " + firstName.is + " " + lastName.is
}

}
