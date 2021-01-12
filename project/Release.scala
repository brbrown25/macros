import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease.ReleasePlugin.autoImport._
import xerial.sbt.Sonatype.SonatypeKeys._

object Release {
  val settings =
    Seq(
      releaseCrossBuild := true,
      sonatypeProfileName := "com.bbrownsound",
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
        releaseStepCommand(s"""sonatypeOpen "${organization.value}" "${name.value} v${version.value}""""),
        releaseStepCommand("+publishSigned"),
        releaseStepCommand("sonatypeReleaseAll"),
        //releasePublishArtifactsAction
        //releaseCrossBuild
        //releaseSnapshotDependencies
        setNextVersion,
        commitNextVersion,
        releaseStepCommand("+publishSigned"),
        pushChanges
      )
    )
}
