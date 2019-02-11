lazy val scVersion = "2.12.8"
lazy val paradiseVersion = "2.1.1"
lazy val common = List(
  scalaVersion := scVersion,
  organization := "com.bbrownsound",
  version := "1.0-SNAPSHOT",
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-compiler" % scVersion,
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "org.slf4j" % "slf4j-simple" % "1.7.25"
  ),
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full))

lazy val macros = (project in file("./macros")).settings(inThisBuild(common), name := "macros")

lazy val examples = (project in file("./examples")).settings(
  inThisBuild(common),
  name := "macros_examples",
  libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.5" % "test")).aggregate(macros).dependsOn(macros)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(common),
    name := "annotation-playground"
  ).aggregate(macros, examples)