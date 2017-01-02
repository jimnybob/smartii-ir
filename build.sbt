name := """smartii-ir"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % Test
)

mainClass in assembly := Some("play.core.server.ProdServerStart")

fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

// Exclude commons-logging because it conflicts with the jcl-over-slf4j
libraryDependencies ~= { _ map {
  case m if m.organization == "com.typesafe.play" =>
    m.exclude("commons-logging", "commons-logging")
  case m => m
}}

assemblyMergeStrategy in assembly := {
  case PathList("play", "core", "server", "ServerWithStop.class") => MergeStrategy.first
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case other => (assemblyMergeStrategy in assembly).value(other)
}
