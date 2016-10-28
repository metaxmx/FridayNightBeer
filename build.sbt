name := """fnb-play"""

version := "0.1_alpha"

scalaVersion := "2.11.8"

lazy val fnbDatamodel = project in file("modules/datamodel")

lazy val fnbStorageMongo = (project in file("modules/storage-mongo")).dependsOn(fnbDatamodel)

lazy val fnbPlay = (project in file("."))
	.aggregate(fnbDatamodel, fnbStorageMongo)
	.dependsOn(fnbDatamodel, fnbStorageMongo)
	.enablePlugins(PlayScala)
	.enablePlugins(SbtWeb)

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.google.inject" % "guice" % "4.1.0",
  "com.google.guava" % "guava" % "19.0",
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.9.4",
  "commons-io" % "commons-io" % "2.5",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.0" exclude("org.apache.logging.log4j", "log4j-api"),
  "org.json4s" %% "json4s-native" % "3.4.2",
  "org.slf4j" % "slf4j-api" % "1.7.21",

  // Test
  specs2 % Test,
  "org.mockito" % "mockito-core" % "2.2.2" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

incOptions := incOptions.value.withNameHashing(true)

updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

routesGenerator := InjectedRoutesGenerator

autoAPIMappings := true

// Run npm install to load JS dependencies and run webpack for resource compression

lazy val npmBuildTask = taskKey[Unit]("Execute the npm build command to build the ui")

npmBuildTask := {
  val operatingSystem = sys.props.getOrElse("os.name", "unknown")
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

net.virtualvoid.sbt.graph.Plugin.graphSettings
