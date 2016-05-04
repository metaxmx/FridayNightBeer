package rest.api_1_0.viewmodels

import org.json4s.{Extraction, JValue}
import rest.Implicits.formats

/**
  * View model trait.
  * Created by Christian Simon on 04.05.2016.
  */
trait ViewModel {

  /**
    * Create [[JValue]] from view model case class.
    * @return result from decomposition
    */
  def toJson: JValue = Extraction decompose this

}
