name := """fnb-datamodel"""

scalaVersion := "2.11.11"

lazy val fnbDataModel = project in file(".")

libraryDependencies ++= {
  val playV = "2.5.12"
  val jodaV = "2.9.9"
  val jsonV = "3.5.1"
  Seq(
    "joda-time"         %  "joda-time"      % jodaV,
    "com.typesafe.play" %% "play-cache"     % playV % Compile,
    "org.json4s"        %% "json4s-native"  % jsonV,
    "org.json4s"        %% "json4s-ext"     % jsonV,

    // Test
    "org.scalatest"     %% "scalatest"      % "3.0.3" % Test
  )
}

autoAPIMappings := true
