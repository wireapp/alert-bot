FROM maven:3.6.3-jdk-8-slim AS build
LABEL description="Wire Alert Bot"
LABEL project="wire-bots:alertbot"

WORKDIR /app

COPY pom.xml ./

RUN mvn verify --fail-never -U

COPY . ./

RUN mvn -Dmaven.test.skip=true package

FROM dejankovacevic/bots.runtime:2.10.3

COPY --from=build /app/target/alertbot.jar /opt/alert/
COPY alertbot.yaml                         /opt/alert/

# create version file
ARG release_version=development
ENV RELEASE_FILE_PATH=/opt/alert/release.txt
RUN echo $release_version > /opt/alert/release.txt
# TODO - uncomment this when migration to JSON logging is finalized
#ENV APPENDER_TYPE=json-console

WORKDIR /opt/alert

EXPOSE  8080 8081 8082

ENTRYPOINT ["java", "-javaagent:/opt/wire/lib/jmx_prometheus_javaagent.jar=8082:/opt/wire/lib/metrics.yaml", "-jar", "alertbot.jar", "server", "/opt/alert/alertbot.yaml"]

