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

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.google.inject" % "guice" % "4.0",
  "com.google.guava" % "guava" % "18.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.8.1",
  "commons-io" % "commons-io" % "2.4",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.11",
  "org.json4s" %% "json4s-native" % "3.3.0",

  // Test
  specs2 % Test,
  "org.mockito" % "mockito-core" % "1.10.17" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test
)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

incOptions := incOptions.value.withNameHashing(true)

updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

routesGenerator := InjectedRoutesGenerator

// Run npm install to load JS dependencies and run webpack for resource compression

lazy val npmBuildTask = taskKey[Unit]("Execute the npm build command to build the ui")

val operatingSystem = sys.props.getOrElse("os.name", "unknown")

npmBuildTask := {
  val cmd = if(operatingSystem contains "Windows")
    "cmd /c npm install"
  else
    "npm install"
  cmd.!
}

watchSources ~= { (ws: Seq[File]) =>
  ws filterNot { path =>
    path.getName.endsWith(".js") || path.getName == "build"
  }
}

compile <<= (compile in Compile) dependsOn npmBuildTask

pipelineStages := Seq(digest, gzip)

PlayKeys.playRunHooks <+= baseDirectory.map(Webpack.apply)
