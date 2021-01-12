FROM hseeberger/scala-sbt:11.0.9.1_1.4.6_2.13.4
WORKDIR .

deps:
  COPY build.sbt ./
  COPY project project
  COPY .scalafmt.conf .scalafmt.conf
  COPY version.sbt version.sbt
  RUN sbt update

unit-test:
  FROM +deps
  COPY macros macros
  RUN sbt ++test

lint-check:
  FROM +deps
  COPY macros macros
  RUN sbt fmtCheck

build:
  FROM +deps
  COPY macros macros
  RUN sbt ++compile

publish:
  FROM +deps
  COPY macros macros
  RUN sbt ++publishSigned

all:
  BUILD +lint-check
  BUILD +build
  BUILD +unit-test
  BUILD +publish
