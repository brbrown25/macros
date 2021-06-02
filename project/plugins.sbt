// Comment to get more information during initialization
logLevel := Level.Warn

// https://github.com/scoverage/sbt-scoverage/releases
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.8.2")

// https://github.com/scoverage/sbt-coveralls/releases
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")
// addSbtPlugin("net.ruippeixotog" % "sbt-coveralls" % "1.3.0") // fork with scoverage/sbt-coveralls#128 merged in

// https://github.com/scalameta/scalafmt
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

// https://github.com/olafurpg/sbt-ci-release
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")

// https://github.com/sbt/sbt-release/releases
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.0.15")

// https://github.com/xerial/sbt-sonatype/releases
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.7")

// https://github.com/sbt/sbt-pgp/releases
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")

//https://github.com/scalacenter/scalafix
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.27")

addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.5.5")

addSbtPlugin("com.47deg" % "sbt-microsites" % "1.3.4")

addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.10.1")
