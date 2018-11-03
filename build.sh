#!/usr/bin/env bash
mvn package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t $DOCKER_USERNAME/alert-bot:latest .
docker push $DOCKER_USERNAME/alert-bot
kubectl delete pod -l name=alert
kubectl get pods -l name=alert
