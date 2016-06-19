name := """fnb-play"""

version := "0.1_alpha"

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

val angularVersion = "2.0.0-rc.2"

libraryDependencies ++= Seq(
  cache,
  "com.google.inject" % "guice" % "4.0",
  "com.google.guava" % "guava" % "18.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.8.1",
  "commons-io" % "commons-io" % "2.4",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.11",
  "org.json4s" %% "json4s-native" % "3.3.0",

  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "jquery" % "1.11.3",
  "org.webjars" % "famfamfam-silk" % "1.3-1",

  "org.webjars.npm" % "ng2-bootstrap" % "1.0.16",

  //angular2 dependencies
  //"org.webjars.npm" % "angular__core" % angularVersion,
  //"org.webjars.npm" % "angular__common" % angularVersion,
  //"org.webjars.npm" % "angular__compiler" % angularVersion,
  //"org.webjars.npm" % "angular__http" % angularVersion,
  //"org.webjars.npm" % "angular__platform-browser" % angularVersion,
  //"org.webjars.npm" % "angular__router" % angularVersion,
  "org.webjars.npm" % "systemjs" % "0.19.26",
  "org.webjars.npm" % "rxjs" % "5.0.0-beta.7",
  //"org.webjars.npm" % "es6-promise" % "3.1.2",
  //"org.webjars.npm" % "es6-shim" % "0.35.0",
  "org.webjars.npm" % "reflect-metadata" % "0.1.3",
  "org.webjars.npm" % "zone.js" % "0.6.12",
  "org.webjars.npm" % "typescript" % "1.9.0-dev.20160529-1.0",

  //tslint dependency
  "org.webjars.npm" % "tslint-eslint-rules" % "1.2.0",
  "org.webjars.npm" % "codelyzer" % "0.0.19",

  // Test
  specs2 % Test,
  "org.mockito" % "mockito-core" % "1.10.17" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test,
  "org.webjars.npm" % "jasmine" % "2.4.1" % Test
)

dependencyOverrides ++= Set(
  "org.webjars.npm" % "minimatch" % "3.0.0",
  "org.webjars.npm" % "glob" % "7.0.3"
)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

EclipseKeys.preTasks := Seq(compile in Compile)

EclipseKeys.withSource := true

EclipseKeys.skipParents in ThisBuild := false

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

// the typescript typing information is by convention in the typings directory
// It provides ES6 implementations. This is required when compiling to ES5.
typingsFile := Some(baseDirectory.value / "typings" / "index.d.ts")

// use the webjars npm directory (target/web/node_modules ) for resolution of module imports of angular2/core etc
resolveFromWebjarsNodeModulesDir := false

// use the combined tslint and eslint rules plus ng2 lint rules
(rulesDirectories in tslint) := Some(List(tslintEslintRulesDir.value,ng2LintRulesDir.value))

routesGenerator := InjectedRoutesGenerator

// enable, to compile the typescript files into a single javascript file
// javaOptions ++= {
//  Seq("-DtsCompileMode=stage")
//}
