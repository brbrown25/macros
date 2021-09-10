import microsites._

ThisBuild / organization := "com.bbrownsound"

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

lazy val allCrossVersions = Seq("2.11.12", "2.12.12", "2.13.5")

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / crossScalaVersions := allCrossVersions
ThisBuild / scalaVersion := crossScalaVersions.value.last
ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11", "adopt@1.15")
ThisBuild / githubWorkflowArtifactUpload := false
ThisBuild / githubWorkflowAddedJobs ++= Seq(
  WorkflowJob(
    "formatting",
    "Check formatting",
    githubWorkflowJobSetup.value.toList ::: List(
      WorkflowStep
        .Run(List(s"sbt ++${crossScalaVersions.value.last} checkAll"), name = Some("Check formatting"))
    )
  ),
  WorkflowJob(
    "coverage",
    "Coverage",
    githubWorkflowJobSetup.value.toList ::: List(
      WorkflowStep.Use(UseRef.Public("actions", "setup-python", "v2"), name = Some("Setup Python")),
      WorkflowStep.Run(List("pip install codecov"), name = Some("Install Codecov")),
      WorkflowStep
        .Sbt(List("coverage", "macros/test", "macros/coverageReport"), name = Some("Calculate test coverage")),
      WorkflowStep.Run(List("codecov"), name = Some("Upload coverage results")),
      WorkflowStep.Run(List("sbt clean coverage test coverageReport"), name = Some("Generate Coverage Report")),
      WorkflowStep.Run(List("sbt coverageAggregate coveralls"), name = Some("Publish Coverage Report"))
    ),
    env = Map(
      "COVERALLS_REPO_TOKEN" -> "${{ secrets.COVERALLS_REPO_TOKEN }}",
      "COVERALLS_FLAG_NAME" -> "MACROS",
      "github-token" -> "${{ secrets.GITHUB_TOKEN }}"
    ),
    scalas = List(crossScalaVersions.value.last),
    cond = Some("github.event_name != 'pull_request'")
  ),
  WorkflowJob(
    "microsite",
    "Microsite",
    githubWorkflowJobSetup.value.toList ::: List(
      WorkflowStep.Use(
        UseRef.Public("ruby", "setup-ruby", "v1"),
        name = Some("Setup Ruby"),
        params = Map("ruby-version" -> "2.6", "bundler-cache" -> "true")
      ),
      WorkflowStep.Run(List("gem install jekyll -v 2.5"), name = Some("Install Jekyll")),
      WorkflowStep.Sbt(List("docs/clean"), name = Some("Clean microsite")),
      WorkflowStep.Sbt(List("docs/makeMicrosite"), name = Some("Build microsite"))
    ),
    scalas = List(crossScalaVersions.value.last),
    cond = Some("github.event_name != 'pull_request'")
  ),
  //eventually do everything with earthly
  WorkflowJob(
    "earthly",
    "Earthly Checks",
    List(
      WorkflowStep.Use(
        UseRef.Public("actions", "checkout", "v2"),
        name = Some("Checkout current branch (full)"),
        params = Map("fetch-depth" -> "0")
      ),
      WorkflowStep.Run(
        List(
          "sudo /bin/sh -c 'wget https://github.com/earthly/earthly/releases/download/v0.5.9/earthly-linux-amd64 -O /usr/local/bin/earthly && chmod +x /usr/local/bin/earthly'"
        ),
        name = Some("Install Earthly")
      ),
      WorkflowStep.Run(List("earthly --version"), name = Some("Earthly version")),
      WorkflowStep.Run(List("earthly +lint-check"), name = Some("Run checks")),
      WorkflowStep.Run(List("earthly +unit-test"), name = Some("Run test")),
    ),
    scalas = List(crossScalaVersions.value.last)
  )
)
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(
    RefPredicate.StartsWith(Ref.Tag("*")),
    RefPredicate.Contains(Ref.Branch("master")),
    RefPredicate.Contains(Ref.Branch("main"))
  )

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

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
  (Compile / console / scalacOptions) ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports", "-Yno-predef"))
  },
  (Test / console / scalacOptions) ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports", "-Yno-predef"))
  },
  (Test / scalacOptions) ~= {
    _.filterNot(Set("-Yno-predef"))
  },
  libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.7.32" % Provided,
    "org.slf4j" % "slf4j-simple" % "1.7.32" % Provided
  ),
  resolvers ++= Seq("public", "snapshots", "releases").map(Resolver.sonatypeRepo),
  (publish / skip) := true
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
    (publish / skip) := false,
    macroSettings ++ Release.settings,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.6",
      "org.scalatest" %% "scalatest" % "3.2.9" % "test"
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
  git.remoteRepo := "git@github.com:brbrown25/macros.git",
  (makeSite / includeFilter) := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md" | "*.svg",
  (Jekyll / includeFilter) := (makeSite / includeFilter).value,
  mdocIn := (LocalRootProject / baseDirectory).value / "docs" / "src" / "main" / "mdoc",
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
    (publish / skip) := true,
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

ThisBuild / coverageEnabled := true
ThisBuild / coverageMinimum := 80
ThisBuild / coverageFailOnMinimum := true

import org.scoverage.coveralls.Imports.CoverallsKeys._

coverallsToken := sys.env.get("COVERALLS_REPO_TOKEN")
