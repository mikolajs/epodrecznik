package net.brosbit4u.lib

import javax.mail._
import javax.mail.internet._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.ExtraData


class MailConfig {
   
   /**For configure mail inside snippet */  
   def configureMailer(host: String, user: String, password: String) {
      this.host = host
      this.user = user
      this.password = password
      saveToDB()
      mkConfig()
   }  
   
   /** For configure in bootstrap if database contain data configuration*/
    def autoConfigure(){
     loadFromDB()
     mkConfig()
   }
  
   def getConfig() = {
     loadFromDB
     (host,user,password)
   }
  
   private var host = ""
   private var user = ""
   private var password = ""
       
   def getConfigTuple() = {
       loadFromDB
       (host, user)
   }
     
   private def loadFromDB() = {
     host = ExtraData.getData("sendmailhost")
     user = ExtraData.getData("sendmailaddress")
     password = ExtraData.getData("sendmailpassword")
   }
   
   private def saveToDB() = {
    ExtraData.setData("sendmailhost", host)
    ExtraData.setData("sendmailaddress", user)
    ExtraData.setData("sendmailpassword", password)
   } 
   
  private def mkConfig(){ 
    println("mkConfig in Mailer user %s password %s host %s".format(this.user, this.password, this.host))
         // Enable TLS support
      System.setProperty("mail.smtp.starttls.enable", "true");
      // Set the host name
      System.setProperty("mail.smtp.host", this.host) // Enable authentication
      System.setProperty("mail.smtp.auth", "true") // Provide a means for authentication. Pass it a Can, which can either be Full or Empty
      Mailer.authenticator = Full(new Authenticator {
       override def getPasswordAuthentication = new PasswordAuthentication( user, password)
      })
     
    }
   
}