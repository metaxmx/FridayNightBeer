/**
  * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  *              F R I D A Y   N I G H T   B E E R
  * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  */

import Dependencies._
import UiBuilding._

/*
 * Settings
 */

val commonSettings = Seq(
  version := "0.1_alpha",
  scalaVersion := "2.12.5",
  organization := "illucIT Software",
  dependencyOverrides ++= versionOverrides
)

val compilerSettings = Seq(
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")
)

val commonResolverSettings = Seq(
  resolvers += Resolver.bintrayRepo("mockito", "maven")
)

val playSettings = Seq(
  //incOptions := incOptions.value.withNameHashing(true),
  updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true),
  routesGenerator := InjectedRoutesGenerator,
  autoAPIMappings := true,
  pipelineStages := Seq(digest, gzip),
  PlayKeys.playRunHooks += baseDirectory.map(webpackHook).value
//  watchSources ~= { (ws: Seq[File]) =>
//    ws filterNot { path =>
//      path.getName.endsWith(".js") || path.getName == "build"
//    }
//  }
)

inThisBuild(
  commonResolverSettings ++
  commonSettings ++
  compilerSettings
)

/*
 * Tasks
 */

lazy val npmInstall = taskKey[Unit]("Execute the npm build command to build the ui")

// Run npm install to load JS dependencies and run webpack for resource compression
val buildUiSettings = Seq(
  npmInstall := runNpmInstall
)

/*
 * Modules
 */

lazy val moduleUtil = (project in file("modules/util"))
  .settings(
    name := "fnb-util",
    libraryDependencies ++= Seq(
      "org.json4s"        %% "json4s-native"  % jsonV,
      "org.json4s"        %% "json4s-ext"     % jsonV,
      "joda-time"         %  "joda-time"      % jodaV
    )
  )

lazy val moduleDataModel = (project in file("modules/datamodel"))
  .settings(
    name := "fnb-datamodel",
    autoAPIMappings := true,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-cache"     % playV,
      "com.google.inject" %  "guice"          % guiceV,
      "com.google.guava"  %  "guava"          % guavaV,
      "org.json4s"        %% "json4s-native"  % jsonV,
      "org.json4s"        %% "json4s-ext"     % jsonV,
      "com.typesafe.akka" %% "akka-actor"     % akkaV,

      // Test
      "org.scalatest"     %% "scalatest"      % scalaTestV % Test,
      "org.mockito"       %  "mockito-core"   % mockitoV   % Test
    )
  )
  .dependsOn(moduleUtil)

lazy val moduleStorageMongo = (project in file("modules/storage-mongo"))
  .settings(
    name := "fnb-storage-mongo",
    libraryDependencies ++= Seq(
      "org.reactivemongo" %% "play2-reactivemongo" 	% reactiveMongoPlayV,
      "com.google.guava" 	%  "guava" 								% guavaV,

      // Test
      "org.scalatest"     %% "scalatest"            % scalaTestV % Test,
      "org.mockito"       %  "mockito-core"         % mockitoV   % Test,
      "org.slf4j"         %  "slf4j-simple"         % slf4jV     % Test
    )
  )
  .dependsOn(moduleDataModel)

lazy val fridayNightBeer = (project in file("."))
  .settings(
    buildUiSettings,
    playSettings,
    name := """FridayNightBeer""",
    libraryDependencies ++= Seq(
      ehcache,
      ws,
      guice,
      "com.google.inject"       %  "guice"                % guiceV,
      "javax.inject"            %  "javax.inject"         % javaXInjectV,
      "com.google.guava"        %  "guava"                % guavaV,
      "joda-time"               %  "joda-time"            % jodaV,
      "commons-io"              %  "commons-io"           % commonsIoV,
      "org.reactivemongo"       %% "play2-reactivemongo"  % reactiveMongoPlayV,
      "org.json4s"              %% "json4s-native"        % jsonV,
      "io.swagger"              %% "swagger-play2"        % swaggerPlayV,
      "org.slf4j"               %  "slf4j-api"            % slf4jV,

      // Test
      "org.scalatest"           %% "scalatest"            % scalaTestV     % Test,
      "org.mockito"             %  "mockito-core"         % mockitoV       % Test,
      "org.scalatestplus.play"  %% "scalatestplus-play"   % scalaTestPlayV % Test
    )
  )
  .aggregate(moduleDataModel, moduleStorageMongo)
  .dependsOn(moduleDataModel, moduleStorageMongo)
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)

