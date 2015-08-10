FROM java:openjdk-8u45-jdk
MAINTAINER stormcat24 <a.yamada24@gmail.com>

RUN apt-get update

# build
COPY . /elastiquartz
RUN cd /elastiquartz && ./gradlew clean
RUN cd /elastiquartz && ./gradlew build
RUN mkdir -p /usr/local/elastiquartz/lib
RUN cp -R /elastiquartz/build/libs/elastiquartz.jar /usr/local/elastiquartz/lib/
RUN rm -rf ~/.gradle

ENTRYPOINT java -jar /usr/local/elastiquartz/lib/elastiquartz.jar

ENV CRON_LOCATION_TYPE="s3" \
    EVENT_TARGET_TYPE="sqs"

EXPOSE 8080
