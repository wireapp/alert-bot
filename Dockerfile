FROM dejankovacevic/bots.runtime:2.10.0

COPY target/alertbot.jar   /opt/alertbot/alertbot.jar
COPY conf/alertbot.yaml    /etc/alertbot/alertbot.yaml

WORKDIR /opt/alertbot

