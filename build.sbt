import microsites._

organization in ThisBuild := "com.bbrownsound"

lazy val paradiseVersion = "2.1.1"
//https://github.com/circe/circe/blob/master/build.sbt
val compilerOptions = Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-Ywarn-unused-import"
)

def priorTo2_13(scVersion: String): Boolean =
  CrossVersion.partialVersion(scVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _ => false
  }

//lazy val versions211 = Seq("2.11.12")
//lazy val versions212 = Seq("2.12.12")
//lazy val versions213 = Seq("2.13.0", "2.13.1", "2.13.2", "2.13.3", "2.13.4")
//lazy val allCrossVersions = versions211 ++ versions212 ++ versions213
lazy val allCrossVersions = Seq("2.11.12", "2.12.8", "2.13.4")
//TODO scala 3 support

lazy val baseSettings = Seq(
  scalacOptions ++= {
    if (priorTo2_13(scalaVersion.value)) compilerOptions
    else
      compilerOptions.flatMap {
        case "-Ywarn-unused-import" => Seq("-Ywarn-unused:imports", "-Wunused")
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
  skip in publish := true
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
        case "-Ywarn-unused-import" => Seq("-Ywarn-unused:imports", "-Wunused")
        case other => Seq(other)
      }
    }
  ),
  crossScalaVersions := allCrossVersions
)

lazy val macros = (project in file("./macros"))
  .settings(
    name := "macros",
    skip in publish := false,
    macroSettings ++ Release.settings,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.2.6" % "test"
    )
  )

lazy val docSettings = Seq(
  micrositeName := "Macros",
  micrositeDescription := "Documentation for macro helper library",
  micrositeAuthor := "Brandon Brown",
  micrositeTwitterCreator := "@brbrown",
  micrositeGithubOwner := "brbrown25",
  micrositeGithubRepo := "macros",
  micrositeGithubLinks := true,
  micrositeGitterChannel := false,
  micrositeShareOnSocial := true,
  micrositeTheme := "pattern",
  micrositePalette := Map(
    "brand-primary" -> "#E05236",
    "brand-secondary" -> "#3F3242",
    "brand-tertiary" -> "#2D232F",
    "gray-dark" -> "#453E46",
    "gray" -> "#837F84",
    "gray-light" -> "#E3E2E3",
    "gray-lighter" -> "#F4F3F4",
    "white-color" -> "#FFFFFF"
  ),
  micrositeFooterText := Some(
    """
      |<p>© 2020 Brandon Brown</p>
      |<p style="font-size: 80%; margin-top: 10px">Website built with <a href="https://47deg.github.io/sbt-microsites/">sbt-microsites © 2020</a></p>
      |""".stripMargin
  ),
  ghpagesNoJekyll := false,
  fork in mdoc := true,
//  git.remoteRepo := "git@github.com:brbrown25/macros.git",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md" | "*.svg",
  includeFilter in Jekyll := (includeFilter in makeSite).value,
  mdocIn := baseDirectory.in(LocalRootProject).value / "docs" / "src" / "main" / "mdoc",
  mdocExtraArguments := Seq("--no-link-hygiene"),
  micrositeGithubToken := sys.env.get("MICROSITE_TOKEN"),
)

lazy val docs = project
  .in(file("macros-docs"))
  .enablePlugins(MdocPlugin)
  .enablePlugins(MicrositesPlugin)
  .settings(macroSettings)
  .settings(
    moduleName := "macros-docs",
    skip in publish := true,
    mdocVariables := Map(
      "SNAPSHOT_VERSION" -> version.value,
      "RELEASE_VERSION" -> "1.0.2",
      "ORG" -> organization.value,
      "NAME" -> "macros",
      "CROSS_VERSIONS" -> allCrossVersions.mkString(", ")
    )
  )
  .settings(docSettings)
  .dependsOn(macros)

lazy val root = (project in file("."))
  .settings(
    name := "annotation-playground",
    baseSettings ++ macroSettings
  )
  .aggregate(macros)

inThisBuild(
  List(
    organization := "com.bbrownsound",
    homepage := Some(url("https://github.com/brbrown25/macros")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "brbrown25",
        "Brandon Brown",
        "brandon@bbrownsound.com",
        url("https://bbrownsound.com")
      )
    ),
    semanticdbEnabled := true, // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision, // use Scalafix compatible version
    scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value),
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
  )
)

addCommandAlias("fix", "scalafixAll")
addCommandAlias("fixCheck", "scalafixAll --check")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("prepare", "fix; fmt")
addCommandAlias("checkAll", "fixCheck; fmtCheck")

coverageEnabled := true
coverageMinimum := 80
coverageFailOnMinimum := true

import org.scoverage.coveralls.Imports.CoverallsKeys._

coverallsToken := sys.env.get("COVERALLS_REPO_TOKEN")
