#!/usr/bin/env bash
docker build -t $DOCKER_USERNAME/alert-bot:staging .
docker push $DOCKER_USERNAME/alert-bot
kubectl delete pod -l name=alert
kubectl get pods -l name=alert
