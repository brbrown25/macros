organization in ThisBuild := "com.bbrownsound"

lazy val paradiseVersion = "2.1.1"

//https://github.com/circe/circe/blob/master/build.sbt
val compilerOptions = Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:experimental.macros"
)

def priorTo2_13(scVersion: String): Boolean =
  CrossVersion.partialVersion(scVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _ => false
  }

lazy val versions211 = Seq("2.11.12")
lazy val versions212 = Seq("2.12.12")
lazy val versions213 = Seq("2.13.0", "2.13.1", "2.13.2", "2.13.3", "2.13.4")
//TODO scala 3 support
//todo add doc site
lazy val baseSettings = Seq(
  scalacOptions ++= {
    if (priorTo2_13(scalaVersion.value)) compilerOptions
    else
      compilerOptions.flatMap {
        case "-Ywarn-unused-import" => Seq("-Ywarn-unused:imports")
        case "-Xfuture" => Nil
        case other => Seq(other)
      }
  },
  scalacOptions in (Compile, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports", "-Yno-predef"))
  },
  scalacOptions in (Test, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports", "-Yno-predef"))
  },
  scalacOptions in Test ~= {
    _.filterNot(Set("-Yno-predef"))
  },
  libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.7.30" % Provided,
    "org.slf4j" % "slf4j-simple" % "1.7.30" % Provided
  ),
  resolvers ++= Seq("public", "snapshots", "releases").map(Resolver.sonatypeRepo),
  skip in publish := true,
  crossScalaVersions := versions211 ++ versions212 ++ versions213
)

lazy val macroSettings: Seq[Setting[_]] = Seq(
  libraryDependencies ++= Seq(
    scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided,
    scalaOrganization.value % "scala-reflect" % scalaVersion.value % Provided
  ) ++ (
    if (priorTo2_13(scalaVersion.value)) {
      Seq(
        compilerPlugin(("org.scalamacros" % "paradise" % paradiseVersion).cross(CrossVersion.full))
      )
    } else Nil
  ),
  scalacOptions ++= (
    if (priorTo2_13(scalaVersion.value)) {
      compilerOptions
    } else {
      compilerOptions.flatMap {
        case "-language:experimental.macros" => Seq("-language:experimental.macros", "-Ymacro-annotations")
        case other => Seq(other)
      }
    }
  ),
  skip in publish := false,
  crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0")
)

lazy val macros = (project in file("./macros"))
  .settings(
    name := "macros",
    macroSettings ++ Release.settings
  )

lazy val examples = (project in file("./examples"))
  .settings(
    name := "examples",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.2.3" % "test"
    ),
    baseSettings ++ macroSettings
  )
  .aggregate(macros)
  .dependsOn(macros)

lazy val root = (project in file("."))
  .settings(
    name := "annotation-playground",
    crossScalaVersions := Nil,
    baseSettings ++ macroSettings
  )
  .aggregate(macros, examples)

// todo add scalafix
// addCommandAlias("fix", "scalafixAll")
// addCommandAlias("fixCheck", "scalafixAll --check")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")
// addCommandAlias("prepare", "fix; fmt")
