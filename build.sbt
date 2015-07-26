name := """fnb-play"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val fnbDatamodel = (project in file("modules/datamodel"))

lazy val fnbDefaultTheme = (project in file("modules/default-theme")).dependsOn(fnbDatamodel)

lazy val fnbPlay = (project in file("."))
	.aggregate(fnbDatamodel, fnbDefaultTheme)
	.dependsOn(fnbDatamodel, fnbDefaultTheme)
	.enablePlugins(PlayScala)
	.enablePlugins(SbtWeb)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  cache,
  "com.google.inject" % "guice" % "3.0",
  "com.google.guava" % "guava" % "15.0",
  "javax.inject" % "javax.inject" % "1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0.play23",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "jquery" % "1.11.3",
  "org.webjars" % "angularjs" % "1.3.15",
  "org.webjars" % "angular-ui-bootstrap" % "0.13.0",
  "org.webjars" % "html5shiv" % "3.7.2",
  "org.webjars" % "respond" % "1.4.2",
  "org.webjars" % "famfamfam-silk" % "1.3-1",
  "org.webjars" % "smart-table" % "2.0.3",
  "org.webjars" % "textAngular" % "1.4.1",
  "org.webjars" % "font-awesome" % "4.3.0-3",
  "org.mockito" % "mockito-core" % "1.10.17" % "test")

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

EclipseKeys.withSource := true

EclipseKeys.skipParents in ThisBuild := false

scalacOptions += "-feature"

