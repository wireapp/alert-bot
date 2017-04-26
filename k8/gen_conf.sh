#!/bin/bash

NAME="alertbot-config"

kubectl delete configmap $NAME
kubectl create configmap $NAME --from-file=../conf