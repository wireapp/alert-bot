FROM eu.gcr.io/wire-bot/bots.runtime:latest

COPY target/alertbot.jar   /opt/alertbot/alertbot.jar
COPY certs/keystore.jks    /opt/alertbot/keystore.jks
COPY metrics.yaml          /opt/alertbot/metrics.yaml

WORKDIR /opt/alertbot

