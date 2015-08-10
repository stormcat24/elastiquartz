FROM java:openjdk-8u45-jdk
MAINTAINER stormcat24 <a.yamada24@gmail.com>

RUN apt-get update

COPY build/libs/elastiquartz.jar /usr/local/elastiquartz/lib/

ENTRYPOINT java -jar /usr/local/elastiquartz/lib/elastiquartz.jar

EXPOSE 8080