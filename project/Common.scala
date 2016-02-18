import sbt._
import Keys._

object Common {
  val appVersion = "0.1-SNAPSHOT"

  var settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.6", "2.11.7"),
    organization := "com.truecar.mleap",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),

    publishTo := (sys.props.get("repository.publish") match {
      case Some(repo) => Some("Custom Repository" at repo)
      case None => None
    })
  )

  settings = sys.props.get("repository.credentials") match {
    case Some(path) =>
      settings :+ (credentials += Credentials(file(path)))
    case None => settings
  }
}