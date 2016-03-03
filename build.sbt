name := "mleap"

updateOptions := updateOptions.value.withCachedResolution(true)

lazy val `root` = project.in(file("."))
  .settings(Common.settings)
  .settings(publishArtifact in (Compile, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false)
  .aggregate(`mleap-core`, `mleap-runtime`, `mleap-learning`, `mleap-spark`)

lazy val `mleap-core` = project.in(file("mleap-core"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(libraryDependencies ++= Dependencies.mleapCoreDependencies)

lazy val `mleap-runtime` = project.in(file("mleap-runtime"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(libraryDependencies ++= Dependencies.mleapRuntimeDependencies)
  .dependsOn(`mleap-core`)

lazy val `mleap-learning` = project.in(file("mleap-learning"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(libraryDependencies ++= Dependencies.mleapLearningDependencies)
  .dependsOn(`mleap-runtime`)

lazy val `mleap-spark` = project.in(file("mleap-spark"))
  .settings(Common.settings)
  .settings(Common.sonatypeSettings)
  .settings(libraryDependencies ++= Dependencies.mleapSparkDependencies)
  .dependsOn(`mleap-runtime`, `mleap-learning`)

lazy val `mleap-benchmark` = project.in(file("mleap-benchmark"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Dependencies.mleapSparkBenchmarkDependencies)
  .settings(testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"))
  .settings(logBuffered := false)
  .settings(parallelExecution in Test := false)
  .dependsOn(`mleap-spark`)
