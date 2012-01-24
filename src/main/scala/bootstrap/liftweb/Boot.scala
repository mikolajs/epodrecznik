package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.mongodb._
import scala.util.logging.Logged

class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
      MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("127.0.0.1", 27017), "vregister"))
    }

    // where to search snippet
    LiftRules.addToPackages("net.brosbit4u")
    Schemifier.schemify(true, Schemifier.infoF _, User)
	
	if (DB.runQuery("select * from users where lastname = 'Administrator'")._2.isEmpty) {
      val u = User.create
      u.lastName("Administrator").level(0).password("123qwerty").email("mail@mail.org").validated(true).save
    }
	
	val isAdmin = If(() => User.loggedIn_? && (User.currentUser.open_!.level.is < 1),
      () => RedirectResponse("/"))
    val isModerator = If(() => User.loggedIn_? && (User.currentUser.open_!.level.is < 2),
      () => RedirectResponse("/"))
    val isUser = If(() => User.loggedIn_? ,
      () => RedirectResponse("/"))
    // Build SiteMap
    def sitemap() = SiteMap(
	  List(
      Menu("Wiadomości") / "index" >> LocGroup("public"), 
	  Menu("Wybór tematu") / "choiceslide" >> LocGroup("public"),
      Menu("Kontakt") / "contact" >> LocGroup("public"),
      Menu("Moje tematy") / "editable" >> LocGroup("public"),
	  Menu("Edycja") / "edit" / ** >> LocGroup("extra") >> Hidden,
      Menu("Pokaz") / "slideshow" / ** >> LocGroup("extra") >> Hidden,
      Menu("Image") / "img" / ** >> LocGroup("extra") >> Hidden,
      Menu("Image upload") / "imagestorage" >> LocGroup("extra") >> Hidden >> isUser,
      Menu("Administrator") / "admin" / "admin" >> LocGroup("admin") >> isAdmin,
      Menu("Przedmioty") / "admin" / "subjects" >> LocGroup("admin") >> isAdmin,
      Menu("Działy") / "admin" / "departments" >> LocGroup("admin") >> isAdmin,
      Menu("Użytkownicy") / "admin" / "users" >> LocGroup("admin") >> isAdmin,
      Menu("Aktualności") / "admin" / "news" >> LocGroup("admin") >> isAdmin
	  ) :::
        User.sitemap: _*)

    LiftRules.setSiteMapFunc(sitemap)

      LiftRules.statelessRewrite.prepend(NamedPF("ClassRewrite") {
		case RewriteRequest(
            ParsePath("edit" :: subjectId :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "edit" :: Nil, Map("id" -> subjectId)  )
        case RewriteRequest(
            ParsePath("slideshow" :: subjectId :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "slideshow" :: Nil, Map("id" -> subjectId)  )
        case RewriteRequest(
            ParsePath("img" :: imgId :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "img" :: Nil, Map("imgId" -> imgId)  )
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
