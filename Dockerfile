FROM java:openjdk-8-jdk-alpine

# build
COPY . /elastiquartz

RUN apk update && \
    apk add --virtual build-dependencies bash && \
    cd /elastiquartz && ./gradlew clean && \
    cd /elastiquartz && ./gradlew build && \
    mkdir -p /usr/local/elastiquartz/lib && \
    cp -R /elastiquartz/build/libs/elastiquartz.jar /usr/local/elastiquartz/lib/ && \
    rm -rf ~/.gradle && \
    rm -rf /elastiquartz && \
    apk del build-dependencies && \
    rm -rf /var/cache/apk/*

ENV CRON_LOCATION_TYPE="s3"
ENV EVENT_TARGET_TYPE="sqs"

EXPOSE 8080

ENTRYPOINT java $JAVA_OPTS -jar /usr/local/elastiquartz/lib/elastiquartz.jar
