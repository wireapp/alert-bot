FROM eu.gcr.io/wire-bot/bots.runtime:latest

COPY target/alertbot.jar   /opt/alertbot/alertbot.jar
COPY conf/alertbot.yaml    /etc/alertbot/alertbot.yaml
COPY metrics.yaml          /opt/alertbot/metrics.yaml

WORKDIR /opt/alertbot

