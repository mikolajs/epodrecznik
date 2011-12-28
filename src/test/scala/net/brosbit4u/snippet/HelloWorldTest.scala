package net.brosbit4u {
package snippet {

import org.specs._
import org.specs.runner.JUnit3
import org.specs.runner.ConsoleRunner
import net.liftweb._
import http._
import net.liftweb.util._
import net.liftweb.common._
import org.specs.matcher._
import org.specs.specification._
import Helpers._



class HelloWorldTestSpecsAsTest extends JUnit3(HelloWorldTestSpecs)
object HelloWorldTestSpecsRunner extends ConsoleRunner(HelloWorldTestSpecs)

object HelloWorldTestSpecs extends Specification {
  val session = new LiftSession("", randomString(20), Empty)
  val stableTime = now



}

}
}
