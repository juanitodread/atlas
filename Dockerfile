FROM openjdk:8-jdk-slim

ENV SBT_VERSION 0.13.13

RUN apt-get update && apt-get install -y \
  curl

RUN \
  curl -L -o sbt-${SBT_VERSION}.deb http://dl.bintray.com/sbt/debian/sbt-${SBT_VERSION}.deb && \
  dpkg -i sbt-${SBT_VERSION}.deb && \
  rm sbt-${SBT_VERSION}.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

WORKDIR /atlas

ADD . /atlas

RUN chmod +x start.sh

CMD ["./start.sh"]