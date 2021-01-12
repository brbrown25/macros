[![License](http://img.shields.io/:license-apache%202.0-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
![CI][Badge-CI]
[![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases]
[![Snapshot Artifacts][Badge-SonatypeSnapshots]][Link-SonatypeSnapshots]
[![Scala Steward][Badge-ScalaSteward]][Link-ScalaSteward]
<!-- [![Coverage](http://codecov.io/github/brbrown25/macros?branch=master)](http://codecov.io/github/brbrown25/macros) -->

# macros
A collection of scala macros for experimentation. this has mainly been for learning about macros, but ProcessTimer
is actually inspired by a recent python project I did where I implemented a timer inspired by
https://medium.com/pythonhive/python-decorator-to-measure-the-execution-time-of-methods-fa04cb6bb36d

sbt "examples/test"
sbt "examples/run"

TODO:
Documentation
Published Module

##Usage
Add the following to your dependencies

* for Release version (currently 2.11 and 2.12):
```scala
libraryDependencies += "com.bbrownsound" %% "macros" % "1.0.1"
```
* for Snapshot version (currently 2.11, 2.12, 2.13):
```scala
libraryDependencies += "com.bbrownsound" %% "macros" % "1.0.2-SNAPSHOT"
```

Note you may also need to add the following resolver
`resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)`

Release:
manual process seems the best for now till I can work out the kinks.

[Badge-CI]: https://github.com/brbrown25/macros/workflows/CI/badge.svg?branch=master "CI"
[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/oss.sonatype.org/com.bbrownsound/macros_2.12.svg "Sonatype Releases"
[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/oss.sonatype.org/com.bbrownsound/macros_2.12.svg "Sonatype Snapshots"
[Badge-ScalaSteward]: https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII= "Scala Steward"
[Link-SonatypeReleases]: https://oss.sonatype.org/content/repositories/releases/com/bbrownsound/macros_2.12/ "Sonatype Releases"
[Link-SonatypeSnapshots]: https://oss.sonatype.org/content/repositories/snapshots/com/bbrownsound/macros_2.12/ "Sonatype Snapshots"
[Link-ScalaSteward]: https://scala-steward.org "Scala Steward"
