[![Build Status](https://travis-ci.com/brbrown25/macros.svg?branch=master)](https://travis-ci.com/brbrown25/macros)
[![Coverage](http://codecov.io/github/brbrown25/macros?branch=master)](http://codecov.io/github/brbrown25/macros)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bbrownsound/macros/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bbrownsound%22)

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
`"com.bbrownsound" %% "macros" % "1.0.1"`
Note you may also need to add the following resolver
`resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)`

Release:
manual process seems the best for now till I can work out the kinks.
