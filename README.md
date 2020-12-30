[![Build Status](https://travis-ci.com/brbrown25/macros.svg?branch=master)](https://travis-ci.com/brbrown25/macros)
[![Coverage](http://codecov.io/github/brbrown25/macros?branch=master)](http://codecov.io/github/brbrown25/macros)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bbrownsound/macros/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bbrownsound%22)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

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
