import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._
import com.trueaccord.scalapb.ScalaPbPlugin

object Common {
  val appVersion = "0.1.3"

  val settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.6", "2.11.7"),
    organization := "com.truecar.mleap",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    resolvers ++= {
      // Only add Sonatype Snapshots if this version itself is a snapshot version
      if(isSnapshot.value) {
        Seq("Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
      } else {
        Seq()
      }
    }
  )

  val sonatypeSettings: Seq[Def.Setting[_]] = Seq(
    publishMavenStyle in publishSigned := true,
    publishTo in publishSigned := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache 2.0 License" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage := Some(url("https://github.com/TrueCar/mleap")),
    scmInfo := Some(ScmInfo(url("https://github.com/TrueCar/mleap.git"),
      "scm:git:git@github.com:TrueCar/mleap.git")),
    developers := List(Developer("hollinwilkins",
      "Hollin Wilkins",
      "hollinrwilkins@gmail.com",
      url("http://hollinwilkins.com")),
      Developer("priannaahsan",
        "Prianna Ahsan",
        "prianna.ahsan@gmail.com",
        url("http://prianna.me")))
  )

  val protobufSettings = ScalaPbPlugin.protobufSettings
}