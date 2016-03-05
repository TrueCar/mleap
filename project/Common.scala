import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._

object Common {
  val appVersion = "0.1-SNAPSHOT"

  var settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.6", "2.11.7"),
    organization := "com.truecar.mleap",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
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
    publishArtifact in Test in publishSigned := false,
    pomIncludeRepository in publishSigned := { _ => false },
    licenses in publishSigned := Seq("Apache 2.0 License" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage in publishSigned := Some(url("https://github.com/TrueCar/mleap"))
  )
}