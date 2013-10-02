name := "epodrecznik.edu.pl"

version := "0.3.5"

organization := "pl.brosbit"

scalaVersion := "2.10.0"

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "staging" at "http://oss.sonatype.org/content/repositories/staging",
                  "releases" at "http://oss.sonatype.org/content/repositories/releases"
                 )


seq(com.github.siasia.WebPlugin.webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.5"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
    "net.liftmodules" %% "lift-jquery-module_2.5" % "2.3",
    "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "container,test",
    "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
     "net.liftweb" %% "lift-mongodb" % liftVersion % "compile",
     "com.h2database" % "h2" % "1.3.167",
    "org.specs2" %% "specs2" % "1.14" % "test",
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts Artifact("javax.servlet", "jar", "jar") 
  )
}




