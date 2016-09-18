package rest.api_1_0.viewmodels

/**
  * Created by Christian Simon on 18.09.2016.
  */
object AdminViewModels {

  /*
   * System Settings
   */

  case class SystemSettingsResult(success: Boolean,
                                  registerEnabled: Boolean,
                                  siteTitle: String,
                                  defaultTheme: String) extends ViewModel

  case class SystemSettingsRequest(registerEnabled: Boolean,
                                   siteTitle: String)

}
