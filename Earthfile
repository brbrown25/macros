FROM hseeberger/scala-sbt:11.0.9.1_1.4.7_2.13.4
WORKDIR .

deps:
  COPY build.sbt ./
  COPY project project
  COPY .scalafmt.conf .scalafmt.conf
  COPY .scalafix.conf .scalafix.conf
  COPY version.sbt version.sbt
  RUN sbt update

unit-test:
  FROM +deps
  COPY macros macros
  RUN sbt ++test

lint-check:
  FROM +deps
  COPY macros macros
  RUN sbt checkAll

build:
  FROM +deps
  COPY macros macros
  RUN sbt ++compile

publish:
  FROM +deps
  COPY macros macros
  RUN \
    --secret PGP_PASSPHRASE=+secrets/PGP_PASSPHRASE \
    --secret PGP_SECRET=+secrets/PGP_SECRET \
    --secret SONATYPE_PASSWORD=+secrets/SONATYPE_PASSWORD \
    --secret SONATYPE_USERNAME=+secrets/SONATYPE_USERNAME \
    --secret CI_RELEASE=+secrets/CI_RELEASE \
    --secret CI_SNAPSHOT_RELEASE=+secrets/CI_SNAPSHOT_RELEASE \
    sbt ci-release

all:
  BUILD +lint-check
  BUILD +build
  BUILD +unit-test
  BUILD +publish
