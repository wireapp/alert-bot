FROM wire/bots.runtime:latest

COPY target/alertbot.jar   /opt/alertbot/alertbot.jar
COPY certs/keystore.jks    /opt/alertbot/keystore.jks

WORKDIR /opt/alertbot

