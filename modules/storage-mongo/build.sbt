name := """fnb-storage-mongo"""

scalaVersion := "2.11.8"

lazy val fnbDatamodel = project in file("../datamodel")

lazy val fnbStorageMongo = (project in file("."))
	.dependsOn(fnbDatamodel)

libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.11.11"
