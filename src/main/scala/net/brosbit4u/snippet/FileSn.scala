package net.brosbit4u.snippet

 import _root_.scala.xml.{ NodeSeq, Text }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    import _root_.net.liftweb.http.{ S, SHtml, FileParamHolder, RequestVar }
    import Helpers._
    import java.awt.image.BufferedImage
    import java.awt.Image
    import javax.imageio.ImageIO
    import java.io.{ File, ByteArrayInputStream, FileOutputStream }
    import scala.util.Random
    import java.util.Date

class FileSn {
  val pathRootImages = "/home/ms/Obrazy/onlinebook"
  object pathToFile extends RequestVar[String]("")
    
  def uploadFile() = {
        var fileHold: Box[FileParamHolder] = Empty
  
        def save() {

          if (fileHold.isEmpty) {
            S.notice("Zapis nieudany")
          } else {
            val dirRoot: File = new File(pathRootImages)
            val randInt = Random.nextInt(99999).abs
            val randDir = randInt % 100
            val date = new Date()
            val imgDir = new File(dirRoot, randDir.toString)
            //sprawdzam czy katalog do którego pasuje hash istnieje
            if (!imgDir.exists) {
              imgDir.mkdir
            }
            S.notice(fileHold.get.fileName)
            val extension = fileHold.get.fileName.split('.').last //jakiś błąd
            val filename =  date.getTime.toString + randDir.toString + "." + extension
            val fileOut = new FileOutputStream(new File(imgDir, filename))
            fileOut.write(fileHold.get.file)
            //zapis - poprawić dodając link do serwera dla nginex
            pathToFile("/img/" + randDir.toString + "/" + filename)
           
          }

        }
        "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
        "#submit" #> SHtml.submit("Dodaj!", save) &
        "#pathToFile" #> <span id="pathToFile">{pathToFile.is}</span>
      }

}