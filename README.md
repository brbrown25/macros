# macros
A collection of scala macros for experimentation. this has mainly been for learning about macros, but ProcessTimer
is actually inspired by a recent python project I did where I implemented a timer inspired by
https://medium.com/pythonhive/python-decorator-to-measure-the-execution-time-of-methods-fa04cb6bb36d

sbt "examples/test"
sbt "examples/run"

TODO:
Documentation
Published Module

"com.bbrownsound" %% "macros" % "1.0.1"
resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

Release:
manual process seems the best for now till I can work out the kinks.