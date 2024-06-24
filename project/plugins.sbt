// Comment to get more information during initialization
logLevel := Level.Warn

// https://github.com/scoverage/sbt-scoverage/releases
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.3")

// https://github.com/scoverage/sbt-coveralls/releases
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.3.5")
// addSbtPlugin("net.ruippeixotog" % "sbt-coveralls" % "1.3.0") // fork with scoverage/sbt-coveralls#128 merged in

// https://github.com/scalameta/scalafmt
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")

// https://github.com/olafurpg/sbt-ci-release
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")

// https://github.com/sbt/sbt-release/releases
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

// https://github.com/xerial/sbt-sonatype/releases
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.10.0")

// https://github.com/sbt/sbt-pgp/releases
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")

//https://github.com/scalacenter/scalafix
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.0")

addSbtPlugin("com.47deg" % "sbt-microsites" % "1.3.4")

addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.24.0")
