FROM dejankovacevic/bots.runtime:2.10.3

COPY target/alertbot.jar   /opt/alert/alertbot.jar
COPY alertbot.yaml         /etc/alert/alertbot.yaml

WORKDIR /opt/alert

EXPOSE  8080 8081 8082
