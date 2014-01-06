/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of E-Podrecznik.edu.pl 
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{ DB, ConnectionManager, ConnectionIdentifier, 
    Schemifier, DefaultConnectionIdentifier, StandardDBVendor }
import _root_.java.sql.{ Connection, DriverManager }
import pl.brosbit.model._
import _root_.net.liftweb.mongodb._
import scala.util.logging.Logged
import pl.brosbit.api._
import pl.brosbit.lib._

object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
       Class.forName("org.h2.Driver")
      val dm = DriverManager.getConnection("jdbc:h2:epodrecznik")
      Full(dm)
    } catch {
      case e: Exception => e.printStackTrace; Empty
    
  }
  }
  def releaseConnection(conn: Connection) { conn.close }
}

class Boot {
    def boot {
		DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

 
      MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("127.0.0.1", 27017), "epodrecznik"))
    

    // where to search snippet
    LiftRules.addToPackages("pl.brosbit")
    Schemifier.schemify(true, Schemifier.infoF _ , User)
    
    LiftRules.statelessDispatch.append({
      case Req("image" :: id :: Nil, _, GetRequest) => () => ImageLoader.icon(id)
      case Req("file" :: id :: Nil, _, GetRequest) => () => FileLoader.file(id)
    })

    if (DB.runQuery("select * from users where lastname = 'Administrator'")._2.isEmpty) {
      val u = User.create
      u.lastName("Administrator").role("a").password("123qwerty").email("mail@mail.org").
      	superUser(true).validated(true).save
    }

    val isAdmin = If(() => User.loggedIn_? && (User.currentUser.openOrThrowException("No user").role.is == "a"),
      () => RedirectResponse("/"))
    	
    val isTeacher = If(() => if(User.loggedIn_?) {
         val r = User.currentUser.openOrThrowException("No user").role.is
        (r == "t" || r == "m" || r == "a") } 
    	else {false}, () => RedirectResponse("/"))
    	
    val isUser = If(() => User.loggedIn_?,
      () => RedirectResponse("/"))
    // Build SiteMap
    def sitemap() = SiteMap(
      List(
        Menu("Wiadomości") / "index" >> LocGroup("public"),
        Menu("Lekcje") / "lessons" >> LocGroup("public"),
        Menu("Kontakt") / "contact" >> LocGroup("public"),
        Menu("Nauczyciel") / "resources" / "index" >> LocGroup("public") >> isTeacher,
        Menu("Kursy") / "resources" / "courses" >> LocGroup("resource") >> isTeacher,  
        Menu("Lekcje") / "resources" / "lessons" >> LocGroup("resource") >> isTeacher,  
        Menu("Hasła") / "resources" / "headwords"  >> LocGroup("resource") >> isTeacher,
        Menu("Prezentacje") / "resources" / "slides" >> LocGroup("resource") >> isTeacher,
        Menu("Artykuły") / "resources" / "documents" >> LocGroup("resource") >> isTeacher,
        Menu("Testy") / "resources" / "quizes" >> LocGroup("resource") >> isTeacher,
        Menu("Zadania") / "resources" / "editquest" >> LocGroup("resource") >> isTeacher,
        Menu("Pliki") / "resources" / "files" >> LocGroup("resource") >> isTeacher,
        Menu("Edycja lekcji") / "resources" / "editlesson" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja tematów") / "resources" / "editheadword" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja Slajdów") / "resources" / "editslide" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja quizów") / "resources" / "editquiz" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edytuj książkę") / "resources" / "editdocument" / ** >> LocGroup("extra") >> Hidden >> isTeacher, 
        //Menu("Wyszukiwanie") / "search" >> LocGroup("extra") >> Hidden, 
        Menu("Pokaz") / "slide" / ** >> LocGroup("extra") >> Hidden,
        Menu("Hasło") / "headword" / ** >> LocGroup("extra") >> Hidden,
        Menu("Czytaj dokument") / "document" / ** >> LocGroup("extra") >> Hidden,
        Menu("Quiz") / "quiz" / ** >> LocGroup("extra") >> Hidden,
        Menu("Wyszukiwanie") / "search"  >> LocGroup("extra") >> Hidden,
        Menu("Image upload") / "imgstorage" >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Thumb upload") / "thumbstorage" >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Administrator") / "admin" / "admin" >> LocGroup("admin") >> isAdmin,
        Menu("Przedmioty") / "admin" / "subjects" >> LocGroup("admin") >> isAdmin,
        Menu("Działy") / "admin" / "departments" >> LocGroup("admin") >> isAdmin,
        Menu("Użytkownicy") / "admin" / "users" >> LocGroup("admin") >> isAdmin,
        Menu("Aktualności") / "admin" / "news" >> LocGroup("admin") >> isAdmin,
        Menu("GC") / "admin" / "gc" >> LocGroup("admin") >> isAdmin,
        Menu("Static") / "static" / **) :::
        User.sitemap: _*)

    LiftRules.setSiteMapFunc(sitemap)

    LiftRules.statelessRewrite.prepend(NamedPF("ClassRewrite") {
      case RewriteRequest(
        ParsePath("resources" :: "editslide" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editslide" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("resources" :: "editheadword" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editheadword" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("resources" :: "editdocument" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editdocument" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
        ParsePath("resources" :: "editquiz" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editquiz" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
        ParsePath("resources" :: "editlesson" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editlesson" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
        ParsePath("slide" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "slide" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("headword" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "headword" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("document" :: documentId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "document" :: Nil, Map("id" -> documentId))
      case RewriteRequest(
        ParsePath("quiz" :: quizId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "quiz" :: Nil, Map("id" -> quizId))
    })
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    //max file upload
    LiftRules.maxMimeSize = 16 * 1024 * 1024
    LiftRules.maxMimeFileSize = 16 * 1024 * 1024
    
    { new MailConfig().autoConfigure() }

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
