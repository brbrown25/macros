import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import xerial.sbt.Sonatype.SonatypeKeys._

object Release {
  val settings =
    Seq(
      releaseCrossBuild := true,
      crossScalaVersions := Seq("2.11.12", "2.12.8"),
      sonatypeProfileName := "com.bbrownsound",
      publishMavenStyle := true,
      publishTo in ThisBuild := sonatypePublishTo.value,
      pomExtra := {
        <url>https://github.com/brbrown25/macros</url>
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <connection>scm:git:git@github.com:brbrown25/macros.git</connection>
          <developerConnection>scm:git:git@github.com:brbrown25/macros.git</developerConnection>
          <url>git@github.com:brbrown25/macros.git</url>
        </scm>
        <developers>
          <developer>
            <id>bbrownsound</id>
            <name>Brandon Brown</name>
            <email>brandon@bbrownsound.com</email>
            <timezone>UTC</timezone>
          </developer>
        </developers>
      },
      releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
        setNextVersion,
        commitNextVersion,
        ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
        pushChanges
      )
    )
}
