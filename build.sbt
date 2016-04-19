name := "mleap"

updateOptions := updateOptions.value.withCachedResolution(true)

lazy val `root` = project.in(file("."))
  .settings(Common.settings)
  .settings(publishArtifact := false)
  .aggregate(`mleap-core`, `mleap-runtime`,
    `mleap-serialization`, `mleap-spark`)

lazy val `mleap-core` = project.in(file("mleap-core"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(libraryDependencies ++= Dependencies.mleapCoreDependencies)

lazy val `mleap-runtime` = project.in(file("mleap-runtime"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(libraryDependencies ++= Dependencies.mleapRuntimeDependencies)
  .dependsOn(`mleap-core`)

lazy val `mleap-serialization` = project.in(file("mleap-serialization"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(Common.protobufSettings)
  .settings(libraryDependencies ++= Dependencies.mleapSerializationDependencies)
  .dependsOn(`mleap-runtime`)

lazy val `mleap-spark` = project.in(file("mleap-spark"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(libraryDependencies ++= Dependencies.mleapSparkDependencies)
  .dependsOn(`mleap-runtime`)

lazy val `mleap-package` = project
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .dependsOn(`mleap-spark`, `mleap-serialization`)

lazy val `mleap-benchmark` = project.in(file("mleap-benchmark"))
  .settings(Common.settings)
  .settings(publishArtifact := false)
  .settings(libraryDependencies ++= Dependencies.mleapBenchmarkDependencies)
  .settings(testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"))
  .settings(logBuffered := false)
  .settings(parallelExecution in Test := false)
  .dependsOn(`mleap-spark`, `mleap-serialization`)
