
lazy val root = (project in file("."))
  .settings(
    name := "FridayNightBeer",
    version := "0.1.0",
    organization := "illucIT Software GmbH",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(

      // Akka
      "com.typesafe.akka" %% "akka-actor" % "2.5.16",
      "com.typesafe.akka" %% "akka-stream" % "2.5.16",
      "com.typesafe.akka" %% "akka-http" % "10.1.5",

      // Config
      "com.typesafe" % "config" % "1.3.2",

      // JSON
      "org.json4s" %% "json4s-native" % "3.6.1",

      // Logging
      "ch.qos.logback" % "logback-classic" % "1.2.3",

      // Testing
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scalamock" %% "scalamock" % "4.1.0" % Test,
      "com.typesafe.akka" %% "akka-testkit" % "2.5.16" % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.16" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test
    )
  )

