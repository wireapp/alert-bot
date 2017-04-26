#!/bin/bash

NAME="alertbot-knows"

AUTH_TOKEN=""
KEYSTORE_PASSWORD=""

kubectl delete secret $NAME
kubectl create secret generic $NAME \
    --from-literal=token=$AUTH_TOKEN \
    --from-literal=keystore_password=$KEYSTORE_PASSWORD