#!/usr/bin/env bash
mvn package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t dejankovacevic/alert-bot:latest .
docker push dejankovacevic/alert-bot
kubectl delete pod -l name=alert -n prod
kubectl get pods -l name=alert -n prod

