FROM anapsix/alpine-java:jdk8
MAINTAINER stormcat24 <a.yamada24@gmail.com>

# build
COPY . /elastiquartz
RUN cd /elastiquartz && ./gradlew clean && \
    cd /elastiquartz && ./gradlew build && \
    mkdir -p /usr/local/elastiquartz/lib && \
    cp -R /elastiquartz/build/libs/elastiquartz.jar /usr/local/elastiquartz/lib/ && \
    rm -rf ~/.gradle && \
    rm -rf /elastiquartz

ENV CRON_LOCATION_TYPE="s3"
ENV EVENT_TARGET_TYPE="sqs"

EXPOSE 8080

ENTRYPOINT java $JAVA_OPTS -jar /usr/local/elastiquartz/lib/elastiquartz.jar
