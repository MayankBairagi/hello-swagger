name := "hello-swagger"

scalaVersion:="2.11.7"

version:="1.1"

libraryDependencies ++= {
  	Seq(		
			"org.scalatest"         % "scalatest_2.11"        	% "2.2.4",
			"ch.qos.logback"        % "logback-classic"       	% "1.1.2",
			"com.typesafe.akka" 	%%"akka-actor"			% "2.4.2",
  		        "com.typesafe.akka" 	%%"akka-http-experimental" 	% "2.4.2",
			"mysql" % "mysql-connector-java" % "5.1.6",
			"org.json4s" %% "json4s-native" % "3.3.0",
			"com.github.swagger-akka-http" %% "swagger-akka-http" % "0.6.2"
)

	}

