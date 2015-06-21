name := """fnb-datamodel"""

scalaVersion := "2.11.6"

lazy val fnbDatamodel = (project in file("."))

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "3.0",
  "com.google.guava" % "guava" % "15.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.3",
  "com.typesafe.play" %% "play-json" % "2.3.7",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23")

EclipseKeys.createSrc := EclipseCreateSrc.Default

EclipseKeys.withSource := true
