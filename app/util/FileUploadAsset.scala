package util

import java.io.{File, FileInputStream}
import java.util.regex.Pattern

import akka.stream.scaladsl.StreamConverters
import controllers.Assets
import controllers.Assets.Asset
import org.apache.commons.io.FilenameUtils
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.{Application, Logger, Mode}
import play.api.http.ContentTypes
import play.api.http.HeaderNames._
import play.api.libs.{Codecs, MimeTypes}
import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.ResponseHeader.{basicDateFormatPattern, httpDateFormat}

import scala.concurrent.Future

/**
  * Utility trait to manage streamed uploads with Last-Modified-Header / ETag / Range Support.
  * Warning: This implementation may depend deeply in
  */
trait FileUploadAsset {

  val appSettings: AppSettings
  val application: Application

  protected def handleFromAppResourcesOrDefault(fileName: String, defaultFileName: Option[String] = None): Action[AnyContent] = {
    resolveAppDataFile(fileName) { dataDirFile =>
      Action.async { implicit request =>
        handleUploadedResource(dataDirFile, fileName)
      }
    } {
      Assets.versioned("/public/appdata-default", Asset(defaultFileName.getOrElse(fileName)))
    }
  }

  protected def handleFromAppResources(path: String): Action[AnyContent] = {
    resolveAppDataFile(path) { dataDirFile =>
      Action.async { implicit request =>
        handleUploadedResource(dataDirFile, FilenameUtils.getName(path))
      }
    } {
      Action {
        NotFound
      }
    }
  }

  private[this] def resolveAppDataFile(path: String)(block: File => Action[AnyContent])(notFound: => Action[AnyContent]) = {
    val file =  new File(appSettings.dataDir, path)
    if (file.exists && file.isFile && file.getCanonicalPath.startsWith(appSettings.dataDir.getCanonicalPath)) {
      block(file)
    } else {
      notFound
    }
  }

  private[this] def handleUploadedResource(appFile: File, filename: String)(implicit request: Request[AnyContent]): Future[Result] = {
    val lastMod = new DateTime(appFile.lastModified)
    val lastModStr = httpDateFormat.print(lastMod)
    val etag = Codecs.sha1(lastModStr + " -> " + appFile.getAbsolutePath)
    lazy val mimeType: String = MimeTypes.forFileName(filename).fold(ContentTypes.BINARY)(addCharsetIfNeeded)

    def cacheableResult(r: Result) = r.withHeaders(
      ETAG -> etag,
      LAST_MODIFIED -> lastModStr,
      CACHE_CONTROL -> cacheControl
    )

    // First check etag. Important, if there is an If-None-Match header, we MUST not check the
    // If-Modified-Since header, regardless of whether If-None-Match matches or not. This is in
    // accordance with section 14.26 of RFC2616.
    val maybeNotModified: Option[Result] = request.headers.get(IF_NONE_MATCH) match {
      case Some(etags) =>
        if (etags.split(',').exists(_.trim == etag)) {
          Some(cacheableResult(NotModified))
        } else {
          None
        }
      case None =>
        for {
          ifModifiedSinceStr <- request.headers.get(IF_MODIFIED_SINCE)
          ifModifiedSince <- parseModifiedDate(ifModifiedSinceStr)
            if !lastMod.isAfter(ifModifiedSince)
        } yield {
          NotModified
        }
    }

    lazy val nonCachedResult = {
      val source = StreamConverters.fromInputStream(() => new FileInputStream(appFile))
      val result = RangeResult.ofSource(appFile.length, source, request.headers.get(RANGE), None, Some(mimeType))
      cacheableResult(result)
    }

    Future.successful(maybeNotModified getOrElse nonCachedResult)
  }

  /*
   * ========== Code Below is adapted from Play Assets implementation, but it was private, so I had to copy it =====================
   */

  private[this] lazy val isProd = application.mode == Mode.Prod

  private[this] val noCacheControl = "no-cache"
  private[this] lazy val defaultCacheControl = appSettings.config.getString("assets.defaultCache").getOrElse("public, max-age=3600")
  private[this] lazy val cacheControl = if (isProd) defaultCacheControl else noCacheControl

  private[this] def addCharsetIfNeeded(mimeType: String): String =
    if (MimeTypes.isText(mimeType)) s"$mimeType; charset=utf-8" else mimeType

  private[this] val standardDateParserWithoutTZ: DateTimeFormatter =
    DateTimeFormat.forPattern(basicDateFormatPattern).withLocale(java.util.Locale.ENGLISH).withZone(DateTimeZone.UTC)
  private[this] val alternativeDateFormatWithTZOffset: DateTimeFormatter =
    DateTimeFormat.forPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z").withLocale(java.util.Locale.ENGLISH).withZone(DateTimeZone.UTC).withOffsetParsed

  /**
    * A regex to find two types of date format. This regex silently ignores any
    * trailing info such as extra header attributes ("; length=123") or
    * timezone names ("(Pacific Standard Time").
    * - "Sat, 18 Oct 2014 20:41:26" and "Sat, 29 Oct 1994 19:43:31 GMT" use the first
    *   matcher. (The " GMT" is discarded to give them the same format.)
    * - "Wed Jan 07 2015 22:54:20 GMT-0800" uses the second matcher.
    */
  private[this] val dateRecognizer = Pattern.compile(
    """^(((\w\w\w, \d\d \w\w\w \d\d\d\d \d\d:\d\d:\d\d)(( GMT)?))|""" +
      """(\w\w\w \w\w\w \d\d \d\d\d\d \d\d:\d\d:\d\d GMT.\d\d\d\d))(\b.*)""")

  /*
   * jodatime does not parse timezones, so we handle that manually
   */
  private[this] def parseModifiedDate(date: String): Option[DateTime] = {
    val matcher = dateRecognizer.matcher(date)
    if (matcher.matches()) {
      val standardDate = Option(matcher.group(3))
      try {
        standardDate map (sd => standardDateParserWithoutTZ.parseDateTime(sd)) orElse {
          val alternativeDate = matcher.group(6) // Cannot be null otherwise match would have failed
          Some(alternativeDateFormatWithTZOffset.parseDateTime(alternativeDate))
        }
//        if (standardDate != null) {
//          Some(standardDateParserWithoutTZ.parseDateTime(standardDate))
//        } else {
//          val alternativeDate = matcher.group(6) // Cannot be null otherwise match would have failed
//          Some(alternativeDateFormatWithTZOffset.parseDateTime(alternativeDate))
//        }
      } catch {
        case e: IllegalArgumentException =>
          Logger.debug(s"An invalid date was received: couldn't parse: $date", e)
          None
      }
    } else {
      Logger.debug(s"An invalid date was received: unrecognized format: $date")
      None
    }
  }
}
