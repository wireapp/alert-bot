#!/usr/bin/env bash
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
mvn package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t $DOCKER_USERNAME/alert-bot:1.4.0 .
docker push $DOCKER_USERNAME/alert-bot
kubectl delete pod -l name=alert
kubectl get pods -l name=alert
