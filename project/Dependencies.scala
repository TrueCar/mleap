import sbt._

object Dependencies {
  val sparkVersion = "1.6.0"
  val akkaVersion = "2.4.1"
  val bundleMlVersion = "0.1.2-SNAPSHOT"

  lazy val baseDependencies = Seq("org.scalatest" %% "scalatest" % "3.0.0-M15" % "test")

  lazy val sparkDependencies = Seq(
    ("org.apache.spark" %% "spark-core" % sparkVersion)
      .exclude("org.mortbay.jetty", "servlet-api")
      .exclude("commons-beanutils", "commons-beanutils-core")
      .exclude("commons-collections", "commons-collections")
      .exclude("commons-logging", "commons-logging")
      .exclude("com.esotericsoftware.minlog", "minlog"),
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-mllib" % sparkVersion,
    "org.apache.spark" %% "spark-catalyst" % sparkVersion).map(_ % "provided")

  lazy val bundleMlDependencies = Seq("ml.bundle" %% "bundle-ml" % bundleMlVersion)

  lazy val mleapCoreDependencies = baseDependencies.union(Seq("org.scalanlp" %% "breeze" % "0.11.2",
    "org.scalanlp" %% "breeze-natives" % "0.11.2"))

  lazy val mleapRuntimeDependencies = Seq()

  lazy val mleapSerializationDependencies = bundleMlDependencies

  lazy val mleapSparkDependencies = sparkDependencies
    .union(bundleMlDependencies)
    .union(Seq("com.typesafe" % "config" % "1.2.1"))
}