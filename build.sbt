import sbt.Keys.libraryDependencies

name := "LogAnal"

version := "1.0"

scalaVersion := "2.11.8"

def dockerSettings(debugPort: Option[Int] = None) = Seq(
  assemblyMergeStrategy in assembly := {
    case r if r.startsWith("reference.conf") => MergeStrategy.concat
    case PathList("META-INF", m) if m.equalsIgnoreCase("MANIFEST.MF") => MergeStrategy.discard
    case x => MergeStrategy.first
  },

  dockerfile in docker := {
    // The assembly task generates a fat JAR file
    val baseDir = baseDirectory.value
    val artifact: File = assembly.value
    val artifactTargetPath = s"/app/${artifact.name}"
    val dockerResourcesDir = baseDir / "dockerscripts"
    val dockerResourcesTargetPath = "/app/"

    new Dockerfile {
      from("java")
      add(artifact, artifactTargetPath)
      copy(dockerResourcesDir, dockerResourcesTargetPath)
      run("chmod","+x",s"/app/entrypoint.sh")
      run("chmod","+x",s"/app/wait-for-it.sh")

      entryPoint(s"/app/entrypoint.sh")
      debugPort match {
        case Some(port) => cmd(s"${name.value}", s"${version.value}", s"$port")
        case None => cmd(s"${name.value}", s"${version.value}")
      }
    }
  },
  imageNames in docker := Seq(
    // Sets the latest tag
    ImageName(s"${name.value}:latest"),

    // Sets a name with a tag that contains the project version
    ImageName(
      namespace = Some(organization.value),
      repository = name.value,
      tag = Some("v" + version.value)
    )
  )
)

lazy val exampleproducer = (project in file("producer"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    libraryDependencies += "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0",
    libraryDependencies += "io.monix" %% "monix-execution" % "2.3.0",
    dockerSettings()
  )

//import scala.sys.process.Process

//val kafkaVersion = "1.1.0"
//val slf4jVersion = "1.7.21"
//val stormVersion = "1.2.1"
//val myScalaVersion = "2.11.8"
//
//name := "loganalysis"
//
//organization := "myhome"
//
//scalaVersion := "2.11.8"
//
//logLevel := Level.Debug
////
////val base_dir = baseDirectory.value
////val base_dir_nit = base_dir / "nifi-integration-tests"
////val pb = Process(s"""nitpath=${base_dir_nit}""").!
//
////val mainClassString = ("FlushTokafka","AggregateToKafka")
//
//scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
//
//
//parallelExecution := false
//
//updateOptions:=updateOptions.value.withCachedResolution(true)
//
//version := "0.1.0"
//
//
//resolvers += Resolver.mavenLocal
////libraryDependencies ++= {
////    Seq(
////
////    )
////  }
//
//
//
//  libraryDependencies ++= Seq(
//    "log4j" % "log4j" % "1.2.17" % "test",
//    "org.slf4j" % "slf4j-api" % slf4jVersion,
//    "org.slf4j" % "slf4j-log4j12" % slf4jVersion % "test",
//    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
//    "org.apache.kafka" %% "kafka" % kafkaVersion exclude("org.slf4j", "slf4j-log4j12"),
//    "org.apache.kafka" % "kafka-clients" % kafkaVersion,
//    "org.apache.storm" % "storm-core" % stormVersion,
//    "org.apache.storm" % "storm-kafka" % stormVersion
//
//  )

////--------------------------------
////---- sbt-assembly settings for spark -----
////--------------------------------
//
//
//mainClass in assembly := Some(mainClassString._1)
//
//assemblyJarName := "run-app.jar"
//
//assemblyMergeStrategy in assembly := {
//  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
//  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
//  case "log4j.properties"                                  => MergeStrategy.discard
//  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
//  case "reference.conf"                                    => MergeStrategy.concat
//  case _                                                   => MergeStrategy.first
//}
//
//assemblyOption in assembly ~= { _.copy(cacheOutput = false) }
//
//assemblyExcludedJars in assembly := {
//  val cp = (fullClasspath in assembly).value
//  cp filter { c =>
//    c.data.getName.startsWith("log4j")
//    c.data.getName.startsWith("slf4j-") ||
//      c.data.getName.startsWith("scala-library")
//  }
//}
//
//
//// Disable tests (they require Spark)
//test in assembly := {}
//
//// publish to artifacts directory
//publishArtifact in(Compile, packageDoc) := false
//
//
//publishTo := Some(Resolver.file("file", new File("artifacts")))
//
//cleanFiles += baseDirectory { base => base / "artifacts" }.value
//
//
//
////--------------------------------
////----- sbt-docker settings ------
////--------------------------------
//
//
//enablePlugins(sbtdocker.DockerPlugin)
//
//dockerfile in docker := {
//  val baseDir = baseDirectory.value
//  val artifact: File = assembly.value
//
//  val sparkHome = "/home/spark"
//  val imageAppBaseDir = "/app"
//  val artifactTargetPath = s"$imageAppBaseDir/${artifact.name}"
//
//  val dockerResourcesDir = baseDir / "docker-resources"
//  val dockerResourcesTargetPath = s"$imageAppBaseDir/"
//
//  new Dockerfile {
//    from("akulbasov1994/spark:1.0")
//    maintainer("akulbasov")
//    env("APP_BASE", s"$imageAppBaseDir")
//    env("APP_CLASS", mainClassString._1)
//    env("APP_CLASS_KAFKA", mainClassString._2)
//    env("SPARK_HOME", sparkHome)
//    copy(artifact, artifactTargetPath)
//    copy(dockerResourcesDir, dockerResourcesTargetPath)
//    //Symlink the service jar to a non version specific name
//    run("chmod", "+x", s"${dockerResourcesTargetPath}/spark-entrypoint.sh")
//    entryPoint(s"${dockerResourcesTargetPath}/spark-entrypoint.sh")
//  }
//}
//buildOptions in docker := BuildOptions(cache = false)
//
//imageNames in docker := Seq(
//  ImageName(
//    namespace = Some(organization.value.toLowerCase),
//    repository = name.value,
//    // We parse the IMAGE_TAG env var which allows us to override the tag at build time
//    tag = Some(sys.props.getOrElse("IMAGE_TAG", default = version.value))
//  )
//)