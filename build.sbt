name := """fnb-play"""

version := "0.1_alpha"

scalaVersion := "2.11.11"

lazy val fnbDataModel = project in file("modules/datamodel")

lazy val fnbStorageMongo = (project in file("modules/storage-mongo")).dependsOn(fnbDataModel)

lazy val fnbPlay = (project in file("."))
	.aggregate(fnbDataModel, fnbStorageMongo)
	.dependsOn(fnbDataModel, fnbStorageMongo)
	.enablePlugins(PlayScala)
	.enablePlugins(SbtWeb)

libraryDependencies ++= {
  val playV = "2.5.12"
  val jodaV = "2.9.9"
  val jsonV = "3.5.1"
  val guavaV = "21.0"
  val reactiveMongoV = "0.12.2"
  val scalaTestV = "3.0.3"
  val mockitoV = "2.7.22"
  Seq(
    cache,
    ws                                                                    exclude("com.google.guava", "guava"),
    "com.google.inject"       %  "guice"                % "4.1.0"         exclude("com.google.guava", "guava"),
    "javax.inject"            %  "javax.inject"         % "1",
    "com.google.guava"        %  "guava"                % guavaV,
    "joda-time"               %  "joda-time"            % jodaV,
    "commons-io"              %  "commons-io"           % "2.5",
    "org.reactivemongo"       %% "play2-reactivemongo"  % reactiveMongoV  exclude("org.apache.logging.log4j", "log4j-api"),
    "org.json4s"              %% "json4s-native"        % jsonV,
    "org.slf4j"               %  "slf4j-api"            % "1.7.25",

    // Test
    "org.scalatest"           %% "scalatest"            % scalaTestV % Test,
    "org.mockito"             %  "mockito-core"         % mockitoV   % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"   % "2.0.0"    % Test
  )
}

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

incOptions := incOptions.value.withNameHashing(true)

updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

routesGenerator := InjectedRoutesGenerator

autoAPIMappings := true

// Run npm install to load JS dependencies and run webpack for resource compression

lazy val npmInstall = taskKey[Unit]("Execute the npm build command to build the ui")

npmInstall := {
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

pipelineStages := Seq(digest, gzip)

PlayKeys.playRunHooks += baseDirectory.map(Webpack.apply).value

