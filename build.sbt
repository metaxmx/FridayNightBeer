name := """fnb-play"""

version := "0.2"

scalaVersion := "2.11.8"

lazy val fnbDatamodel = project in file("modules/datamodel")

lazy val fnbDefaultTheme = (project in file("modules/default-theme")).dependsOn(fnbDatamodel)

lazy val fnbStorageMongo = (project in file("modules/storage-mongo")).dependsOn(fnbDatamodel)

lazy val fnbPlay = (project in file("."))
	.aggregate(fnbDatamodel, fnbStorageMongo, fnbDefaultTheme)
	.dependsOn(fnbDatamodel, fnbStorageMongo, fnbDefaultTheme)
	.enablePlugins(PlayScala)
	.enablePlugins(SbtWeb)

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  cache,
  specs2 % Test,
  "com.google.inject" % "guice" % "4.0",
  "com.google.guava" % "guava" % "18.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.8.1",
  "commons-io" % "commons-io" % "2.4",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.11",
  "org.json4s" %% "json4s-native" % "3.3.0",
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

EclipseKeys.preTasks := Seq(compile in Compile)

EclipseKeys.withSource := true

EclipseKeys.skipParents in ThisBuild := false

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

