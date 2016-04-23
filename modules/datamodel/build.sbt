name := """fnb-datamodel"""

scalaVersion := "2.11.8"

lazy val fnbDatamodel = (project in file(".")).settings(scalaVersion := "2.11.7")

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.0",
  "com.google.guava" % "guava" % "18.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.8.1",
  "com.typesafe.play" %% "play-json" % "2.5.2",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.11")

EclipseKeys.createSrc := EclipseCreateSrc.Default

EclipseKeys.withSource := true
