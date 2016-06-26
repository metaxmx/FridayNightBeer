name := """fnb-theme-default"""

scalaVersion := "2.11.8"

lazy val fnbDatamodel = project in file("../datamodel")

lazy val fnbDefaultTheme = (project in file("."))
	.dependsOn(fnbDatamodel)
	.enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

//includeFilter in (Assets, LessKeys.less) := "*.less"

//excludeFilter in (Assets, LessKeys.less) := "_*.less"
