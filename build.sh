#!/usr/bin/env bash
docker build -t $DOCKER_USERNAME/alert-bot:1.5.0 .
docker push $DOCKER_USERNAME/alert-bot
kubectl delete pod -l name=alert -n prod
kubectl get pods -l name=alert -n prod
