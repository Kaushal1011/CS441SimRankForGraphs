import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

val scalaTestVersion = "3.2.11"
val logbackVersion = "1.2.10"
val sfl4sVersion = "2.0.0-alpha5"
val scalaParCollVersion = "1.0.4"
val typeSafeConfigVersion = "1.4.2"
val netBuddyVersion = "1.14.4"
val typesafeConfigVersion = "1.4.1"
val apacheCommonIOVersion = "2.11.0"
val scalacticVersion = "3.2.9"
val hadoopVersion = "3.2.1"
val snakeyamlVersion = "2.1"

lazy val commonDependencies = Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % scalaParCollVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalatestplus" %% "mockito-4-2" % "3.2.12.0-RC2" % Test,
  "com.typesafe" % "config" % typeSafeConfigVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "net.bytebuddy" % "byte-buddy" % netBuddyVersion,
  "commons-io" % "commons-io" % apacheCommonIOVersion,
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % hadoopVersion,
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion,
  "org.apache.hadoop" % "hadoop-client" % hadoopVersion,
  "org.scalactic" %% "scalactic" % scalacticVersion,
  "org.yaml" % "snakeyaml" % snakeyamlVersion,
  "software.amazon.awssdk" % "s3" % "2.19.31",
  "software.amazon.awssdk" % "netty-nio-client" % "2.19.31"

)

resolvers += Resolver.jcenterRepo


val jarName = "SimRankForGraphs.jar"
//assembly/assemblyJarName := jarName

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

lazy val root = (project in file("."))
  .settings(
    name := "CS441SimRankForGraphs",
    libraryDependencies ++= commonDependencies
  )
