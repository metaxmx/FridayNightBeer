name := """fnb-datamodel"""

scalaVersion := "2.11.11"

lazy val fnbDataModel = project in file(".")

libraryDependencies ++= {
  val playV = "2.5.12"
  val jodaV = "2.9.9"
  val jsonV = "3.5.1"
  val guavaV = "21.0"
  val scalaTestV = "3.0.3"
  val mockitoV = "2.7.22"
  Seq(
    "joda-time"         %  "joda-time"      % jodaV,
    "com.typesafe.play" %% "play-cache"     % playV exclude("com.google.inject", "guice"),
    "com.google.inject" %  "guice"          % "4.1.0" exclude("com.google.guava", "guava"),
    "com.google.guava"  %  "guava"          % guavaV,
    "org.json4s"        %% "json4s-native"  % jsonV,
    "org.json4s"        %% "json4s-ext"     % jsonV,

    // Test
    "org.scalatest"     %% "scalatest"      % scalaTestV % Test,
    "org.mockito"       %  "mockito-core"   % mockitoV % Test
  )
}

autoAPIMappings := true
