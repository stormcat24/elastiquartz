FROM java:openjdk-8u45-jdk
MAINTAINER stormcat24 <a.yamada24@gmail.com>

RUN apt-get update

COPY build/libs/elastiquartz-0.0.1-SNAPSHOT.jar /usr/local/elastiquartz/lib/

ENTRYPOINT java -jar /usr/local/elastiquartz/lib/elastiquartz-0.0.1-SNAPSHOT.jar

EXPOSE 8080