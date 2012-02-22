/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet 

    import scala.xml.{ NodeSeq, Text }
import _root_.net.liftweb._
import util._
import common._
import Helpers._
import http.{ S, SHtml, FileParamHolder, RequestVar }
import java.util.Date
import java.awt.image.BufferedImage
import java.awt.Image
import javax.imageio.ImageIO
import java.io.{ File, ByteArrayInputStream, FileOutputStream }
import com.mongodb.gridfs._
import mongodb._
import java.io.ByteArrayOutputStream
    /**
     * snipet ma dostarczać grafikę na stronę oraz obsługiwać zapis zdjęć i dostarczenie
     * ich do edytora - w przysłości obsługa plików statycznych
     */
    class FileSn {

      object linkpath extends RequestVar[String]("")
      val pathRootImages = "/home/ms/epodreczniki"
      val pathNewsImages = "/news"

        
        
      def addImg() = {
        var fileHold: Box[FileParamHolder] = Empty
        var mimeType = ""
        var mimeTypeFull = ""
        def isCorrect = fileHold match {
          case Full(FileParamHolder(_, mime, _, data)) if mime.startsWith("image/") => {
            mimeType = "." + mime.split("/")(1)
            mimeTypeFull = mime.toString
            S.notice(mime.toString) //tutaj do dać odczyt mime i rozszerzenie
            true
          }
          case Full(_) => {
            S.error("Nieprawidłowy format pliku!")
            false
          }
          case _ => {
            S.error("Brak pliku?")
            false
          }
        }

        def save() {

          if (isCorrect) {

            val dirRoot: File = new File(pathRootImages)
            val stampInt = new Date().getTime()
            val remoteIp = S.containerRequest.map(_.remoteAddress).openOr("127.0.0.1").split('.').map(_.toInt).map(_.toHexString).mkString
            val aRandom = (new java.util.Random).nextInt().toHexString
            val subDirPath = ((stampInt % 200) + 1).toString
            val imgDir = new File(dirRoot, subDirPath)
            //sprawdzam czy katalog do którego pasuje hash istnieje
            if (!imgDir.exists) {
              imgDir.mkdir
            }
            val fileName =  stampInt.toHexString + remoteIp + aRandom + mimeType
            val link = imgDir.getAbsolutePath + "/" + fileName
            var imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(fileHold.get.file))
            //tutaj przeskalowanie
            var imBox: Box[BufferedImage] = Empty
            val w = imageBuf.getWidth
            val h = imageBuf.getHeight
            val MAXSIZE = 800
            if (w > MAXSIZE || h > MAXSIZE) {
              if (w > h) {
                val im: java.awt.Image = imageBuf.getScaledInstance(MAXSIZE, (h.toDouble * MAXSIZE.toDouble / w.toDouble).toInt, Image.SCALE_SMOOTH)
                //val graf2D = imageBuf.createGraphics
                //graf2D.scale(1.0, 500.0/w.toDouble)
                //imageBuf.getGraphics.translate(500, (h.toDouble * 500.0/w.toDouble).toInt)
                imBox = Full(new BufferedImage(MAXSIZE, (h.toDouble * MAXSIZE.toDouble / w.toDouble).toInt, BufferedImage.TYPE_INT_ARGB))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
                //imageBuf = im.asInstanceOf[BufferedImage]
              } else {
                val im: java.awt.Image = imageBuf.getScaledInstance((w.toDouble * MAXSIZE.toDouble / h.toDouble).toInt, MAXSIZE, Image.SCALE_SMOOTH)
                imBox = Full(new BufferedImage((w.toDouble * MAXSIZE.toDouble / h.toDouble).toInt, MAXSIZE, BufferedImage.TYPE_INT_ARGB))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
              }
            } else {
              imBox = Full(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB))
              imBox.get.getGraphics.drawImage(imageBuf, 0, 0, null)
            }
            //dodać do imbox obrazek gdy jest mały!
            //zapisx$1
            println("========================================= mime: " + mimeType)
            if (ImageIO.write(imBox.get, mimeType.substring(1), new File(imgDir, fileName))) {
              linkpath(serverPath + "/imgdata/" + fileName)
              S.notice("Plik został zapisany")
            } else {
              S.notice("Zapis nieudany")
            }
          }
        }
        "img [src]" #> linkpath.is &
        "#path" #> SHtml.text(linkpath.is,x=>x,"type"->"hidden") &
        "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
        "#submit" #> SHtml.submit("Dodaj!", save) 
          

      }
      
      
      def addNewsImg() = {
        var fileHold: Box[FileParamHolder] = Empty
        var mimeType = ""
        var mimeTypeFull = ""
         def isCorrect = fileHold match {
          case Full(FileParamHolder(_, mime, _, data)) if mime.startsWith("image/") => {
            mimeType = "." + mime.split("/")(1)
            mimeTypeFull = mimeType.toString()
            S.notice(mime.toString) 
            if (mimeType == ".png" || mimeType == ".jpeg" || mimeType == ".gif") true
            else {
              println("Nieprawidłowy format pliku!")
              S.error("Nieprawidłowy format pliku!")
              false
            }
          }
          case Full(_) => {
            println("Nieprawidłowy format pliku!")
            S.error("Nieprawidłowy format pliku!")
            false
          }
          case _ => {
            println("Brak pliku")
            S.error("Brak pliku?")
            false
          }
        }
        
        def save() {
          if (isCorrect){
           var fileName =  fileHold.get.fileName.split('.').dropRight(1).mkString
           if (fileName.isEmpty) fileName = scala.util.Random.nextLong.toString
           var imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(fileHold.get.file))
           var mime = mimeType(1)
           val imBox:Box[BufferedImage] = getImageBox(500, imageBuf,mime)
           var outputStream = new ByteArrayOutputStream()
           ImageIO.write(imBox.get, mimeType.substring(1),outputStream)
           val inputStream = new ByteArrayInputStream(outputStream.toByteArray())
           MongoDB.use(DefaultMongoIdentifier) { db =>
           val fs = new GridFS(db)
           val inputFile = fs.createFile(inputStream)
           inputFile.setContentType(mimeTypeFull)
           inputFile.setFilename(fileName + mimeType)
           inputFile.save
           linkpath("/image/" + inputFile.getId().toString() + mimeType)
           
           }
            /*
            val imgDir: File = new File(pathRootImages + pathNewsImages)
            val fileName =  fileHold.get.name
            
            val link = imgDir.getAbsolutePath + "/" + fileName
            var imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(fileHold.get.file))
            val imBox:Box[BufferedImage] = getImageBox(500, imageBuf)
             if (ImageIO.write(imBox.get, mimeType.substring(1), new File(imgDir, fileName))) {
              linkpath(serverPath + "images/news/" + fileName)
              S.notice("Plik został zapisany")
            } else {
              S.notice("Zapis nieudany")
            }*/
          }
          
        } 
        
        "img [src]" #> linkpath.is &
        "#path" #> SHtml.text(linkpath.is,x=>x,"type"->"hidden") &
        "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
        "#submit" #> SHtml.submit("Dodaj!", save) 
      } 
      
      def getImageBox(maxSize:Int, imageBufIn: BufferedImage,mime:Char):Box[BufferedImage] = {
         var imageBuf: BufferedImage = imageBufIn
            //tutaj przeskalowanie
            var imBox: Box[BufferedImage] = Empty
            val w = imageBuf.getWidth
            val h = imageBuf.getHeight
            val bufferedImageTYPE = if(mime == 'j') BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
            if (w > maxSize || h > maxSize) {
              if (w > h) {
                val im: java.awt.Image = imageBuf.getScaledInstance(maxSize, (h.toDouble * maxSize.toDouble / w.toDouble).toInt, Image.SCALE_SMOOTH)
                //val graf2D = imageBuf.createGraphics
                //graf2D.scale(1.0, 500.0/w.toDouble)
                //imageBuf.getGraphics.translate(500, (h.toDouble * 500.0/w.toDouble).toInt)
                imBox = Full(new BufferedImage(maxSize, (h.toDouble * maxSize.toDouble / w.toDouble).toInt, bufferedImageTYPE))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
                //imageBuf = im.asInstanceOf[BufferedImage]
              } else {
                val im: java.awt.Image = imageBuf.getScaledInstance((w.toDouble * maxSize.toDouble / h.toDouble).toInt, maxSize, Image.SCALE_SMOOTH)
                imBox = Full(new BufferedImage((w.toDouble * maxSize.toDouble / h.toDouble).toInt, maxSize, bufferedImageTYPE))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
              }
            } else {
              imBox = Full(new BufferedImage(w, h, bufferedImageTYPE))
              imBox.get.getGraphics.drawImage(imageBuf, 0, 0, null)
            }
         imBox
      }
      
      
       lazy val serverPath = {
        S.request match {
          case Full(r) => r.hostAndPath
          case _ => "add host name"
        }
      }

      //niezaimlpementowane - zrobić osobny moduł dla crona
      def cleanImages(node: NodeSeq): NodeSeq = {
        //przeszukiwanie wszystkich Post, Page i Comments i wyszukanie wszystkich 
        val n = <h1>Nieużywane obrazki  zostały usunięte</h1>
        n
      }
    }
    
    