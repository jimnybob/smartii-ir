enablePlugins(PlayScala)

name := """smartii-ir"""

organization := "uk.co.smartii"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.12"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.typelevel" %% "cats" % "0.8.1",
  "commons-net"    % "commons-net" % "3.5",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % Test,
  "com.typesafe.akka"            %% "akka-testkit" % akkaVersion % Test
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

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)

publishTo := {
  val nexus = "http://repo.smartii.co.uk:8081/nexus/content/repositories"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "/snapshots")
  else
    Some("releases"  at nexus + "/releases")
}