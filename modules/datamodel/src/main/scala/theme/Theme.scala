package theme

trait Theme {

  def label: String
  
  def id: String
  
  def cssClass: String
  
  def stylesheets: Seq[String]
  
  def javascripts: Seq[String]
  
}