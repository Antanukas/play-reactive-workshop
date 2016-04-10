name := """play-reactive-workshop"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  evolutions,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.h2database" % "h2" % "1.4.191",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",

  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.webjars" % "jquery" % "2.2.3"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
