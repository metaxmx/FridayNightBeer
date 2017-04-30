name := """fnb-storage-mongo"""

scalaVersion := "2.11.11"

lazy val fnbDataModel = project in file("../datamodel")

lazy val fnbStorageMongo = (project in file("."))
	.dependsOn(fnbDataModel)

libraryDependencies ++= {
	val guavaV = "21.0"
	val reactiveMongoV = "0.12.2"
	Seq(
		"org.reactivemongo" %% "play2-reactivemongo" 	% reactiveMongoV	exclude("org.apache.logging.log4j", "log4j-api"),
		"com.google.guava" 	%  "guava" 								% guavaV
	)
}
