name := """fnb-storage-mongo"""

scalaVersion := "2.11.8"

lazy val fnbDatamodel = project in file("../datamodel")

lazy val fnbStorageMongo = (project in file("."))
	.dependsOn(fnbDatamodel)

libraryDependencies ++= Seq(
	"org.reactivemongo" %% "play2-reactivemongo" % "0.12.0" exclude("org.apache.logging.log4j", "log4j-api"),
	"com.google.guava" % "guava" % "20.0"
)
