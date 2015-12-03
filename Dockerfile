FROM java:openjdk-8u45-jdk
MAINTAINER stormcat24 <a.yamada24@gmail.com>

# build
COPY . /elastiquartz
RUN apt-get update && \
    cd /elastiquartz && ./gradlew clean && \
    cd /elastiquartz && ./gradlew build && \
    mkdir -p /usr/local/elastiquartz/lib && \
    cp -R /elastiquartz/build/libs/elastiquartz.jar /usr/local/elastiquartz/lib/ && \
    rm -rf ~/.gradle

#jolokia
COPY jolokia-jvm-1.3.1-agent.jar /usr/local/elastiquartz/lib/

ENV JAVA_OPTS="$JAVA_OPTS -javaagent:/usr/local/elastiquartz/lib/jolokia-jvm-1.3.1-agent.jar=port=8778,host=0.0.0.0"
ENV CRON_LOCATION_TYPE="s3"
ENV EVENT_TARGET_TYPE="sqs"

EXPOSE 8080 8778

ENTRYPOINT java $JAVA_OPTS -jar /usr/local/elastiquartz/lib/elastiquartz.jar
