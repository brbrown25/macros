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
    "org.slf4j" % "slf4j-api" % "1.7.25" % Provided,
    "org.slf4j" % "slf4j-simple" % "1.7.25" % Provided
  ),
  resolvers ++= Seq("public", "snapshots", "releases").map(Resolver.sonatypeRepo)
)

lazy val macroSettings: Seq[Setting[_]] = Seq(
  libraryDependencies ++= Seq(
    scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided,
    scalaOrganization.value % "scala-reflect" % scalaVersion.value % Provided
  ) ++ (
    if (priorTo2_13(scalaVersion.value)) {
      Seq(
        compilerPlugin(("org.scalamacros" % "paradise" % paradiseVersion).cross(CrossVersion.patch))
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
  )
)

lazy val macros = (project in file("./macros"))
  .settings(
    name := "macros",
    skip in publish := false,
    macroSettings ++ Release.settings
  )

lazy val examples = (project in file("./examples"))
  .settings(
    baseSettings ++ macroSettings,
    name := "examples",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"
    )
  )
  .aggregate(macros)
  .dependsOn(macros)

lazy val root = (project in file("."))
  .settings(inThisBuild(baseSettings), name := "annotation-playground")
  .aggregate(macros, examples)
