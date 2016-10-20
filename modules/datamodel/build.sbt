name := """fnb-datamodel"""

scalaVersion := "2.11.8"

lazy val fnbDatamodel = project in file(".")

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.1.0",
  "com.google.guava" % "guava" % "19.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.9.4",
  "com.typesafe.play" %% "play-cache" % "2.5.8",
  "org.json4s" %% "json4s-native" % "3.4.0",
  "org.json4s" %% "json4s-ext" % "3.4.0"
)

autoAPIMappings := true
