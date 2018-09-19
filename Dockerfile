FROM dejankovacevic/bots.runtime:2.10.0

COPY target/alertbot.jar   /opt/alert/alertbot.jar
COPY alertbot.yaml         /etc/alert/alertbot.yaml

WORKDIR /opt/alert

