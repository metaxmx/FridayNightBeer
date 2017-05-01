name := """fnb-storage-mongo"""

scalaVersion := "2.11.11"

lazy val fnbDataModel = project in file("../datamodel")

lazy val fnbStorageMongo = (project in file("."))
	.dependsOn(fnbDataModel)

libraryDependencies ++= {
	val guavaV = "21.0"
	val reactiveMongoV = "0.12.2"
	val scalaTestV = "3.0.3"
	val mockitoV = "2.7.22"
	Seq(
		"org.reactivemongo" %% "play2-reactivemongo" 	% reactiveMongoV	exclude("org.apache.logging.log4j", "log4j-api"),
		"com.google.guava" 	%  "guava" 								% guavaV,

		// Test
		"org.scalatest"     %% "scalatest"            % scalaTestV % Test,
		"org.mockito"       %  "mockito-core"         % mockitoV % Test,
		"org.slf4j"         %  "slf4j-simple"         % "1.7.25" % Test
	)
}
