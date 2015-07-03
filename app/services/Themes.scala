package services

import theme._
import java.util.ServiceLoader
import scala.collection.JavaConversions._
import play.api.Logger
import com.typesafe.config._
import com.typesafe.config.ConfigException.BadValue

object Themes {

  val defaultThemeConfigKey = "fnb.defaulttheme"

  private def loadThemes: Seq[Theme] = {
    val loadedThemes = ServiceLoader.load(classOf[Theme], classOf[Theme].getClassLoader).toSeq
    for (theme <- loadedThemes) {
      Logger.info(s"Found Theme '${theme.id}' (${theme.label}) defined in ${theme.getClass}")
    }
    // loadedThemes
    // TODO: FIx Theme Loading
    Seq(new DefaultTheme)
  }

  lazy val themes = loadThemes

  lazy val themesById = (themes map { theme => theme.id -> theme }).toMap

  lazy val defaultTheme: Theme = {
    val config = ConfigFactory.load()
    val defaultThemeId = config.getString(defaultThemeConfigKey)
    val theme = getTheme(defaultThemeId)
    theme.getOrElse(throw new BadValue(config.getValue(defaultThemeConfigKey).origin(),
      defaultThemeConfigKey, s"Theme $defaultThemeId not found"))
  }

  def getTheme(id: String): Option[Theme] = themesById get id

}