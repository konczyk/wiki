name := "wiki"
scalaVersion := "2.12.2"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.6"

libraryDependencies += "org.elasticsearch" % "elasticsearch" % "5.4.0"
libraryDependencies += "org.elasticsearch.client" % "transport" % "5.4.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.8.2"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
libraryDependencies += "org.json4s" % "json4s-native_2.12" % "3.5.2"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % "test"
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test"
