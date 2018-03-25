/**
  * Central Dependencies for the SBT build.
  */
object Dependencies {

  import play.core.PlayVersion

  val playV = PlayVersion.current
  val swaggerPlayV = "1.6.0"

  val reactiveMongoV = "0.13.0"
  val reactiveMongoPlayV = s"$reactiveMongoV-play26"

  val javaXInjectV = "1"
  val guavaV = "22.0"
  val guiceV = "4.1.0"
  val commonsIoV = "2.5"

  val jsonV = "3.5.3"
  val jodaV = "2.9.9"
  val slf4jV = "1.7.25"

  val akkaV = "2.5.11"

  val scalaTestV = "3.0.5"
  val mockitoV = "2.17.1"
  val scalaTestPlayV = "3.1.2"

  import sbt.stringToOrganization

  // Version overrides
  lazy val versionOverrides = Seq(
    "com.google.guava"  %  "guava"       % guavaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-actor"  % akkaV
  )

}
