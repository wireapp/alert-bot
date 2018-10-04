# Alert-bot
[![Build Status](https://travis-ci.org/wireapp/alert-bot.svg?branch=master)](https://travis-ci.org/wireapp/alert-bot)

This is alert bot for Wire.

## Alert manager config
```
...

receivers:
  - name: 'alertbot'
    webhook_configs:
    - url: https://services.wire.com/alert/prometheus
      send_resolved: true
      http_config:
          bearer_token: '$ALERT_PROMETHEUS_TOKEN'
          tls_config:
            insecure_skip_verify: true
...

```
## How to send _Prometheus_ alerts manually
```
curl 'localhost:8080/alert/prometheus' \
    -H "Authorization:Bearer $ALERT_PROMETHEUS_TOKEN" \
    -H "Content-Type:Application/Json" \
    -d @examples/prometheus.json
```

## How to send a _Simple_ alert manually
```
curl 'localhost:8080/alert/simple' \
    -H "Authorization:Bearer $ALERT_PROMETHEUS_TOKEN" \
    -H "Content-Type:Application/Json" \
    -d '{ "message" : "This is just a test" }'
```

## How the rendered alerts look like


## How to filter out alerts
```
/label add service ibis
```
This will make that only warnings that contain _label_ `service=ibis` would be displayed
</b>
```
/label remove service ibis
```

## Whitelist users that can receive alerts
comma separated list of Wire `username` in `whitelist` in the `config`.
Leave the list empty if you want to allow everybody to join.            