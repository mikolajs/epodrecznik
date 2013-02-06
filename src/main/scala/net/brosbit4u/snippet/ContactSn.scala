package net.brosbit4u.snippet

import _root_.net.liftweb._
import util._
import common._
import http.{ S, SHtml }
import Mailer._
import Helpers._
import _root_.net.brosbit4u.model.ExtraData

class ContactSn {
	 def contact() = {
    var theme = ""
    var email = ""
    var content = ""
    def save() = {
      val adminMail = ExtraData.getData("sendmailaddress")
      if(theme.length > 0 && content.length > 5) {
          println("wysyłanie maila")
         val body = content + "\n" + "----------\n Informacja wysłana ze strony przez: " + email
         Mailer.sendMail(From("zestrony@epodrecznik.edu.pl"), Subject(theme), 
          To(adminMail), PlainMailBodyType(body))
           S.notice("Wysłano Email")
      } 
      else S.notice("Nie wysłano. Uzupełnij wszystkie pola.")

    }

    "#theme" #> SHtml.text(theme, x => theme = x.trim) &
      "#email" #> SHtml.text(email, x => email = x.trim) &
      "#content" #> SHtml.textarea(content, x=> content = x.trim) &
      "#save" #> SHtml.submit("WYŚLIJ", save)
  }
}