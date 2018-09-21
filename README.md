# Alert-bot
This is alert bot for Wire.

## Prometheus config
```
...

receivers:
  - name: 'alertbot'
    webhook_configs:
    - url: https://services.wire.com/alert/prometheus
      send_resolved: false
      http_config: your.http_config
      
...

```
## How to send Prometheus alerts manually
```
curl 'localhost:8080/alert/prometheus' -H "Authorization:Bearer $ALERT_PROMETHEUS_TOKEN" -H'content-type:application/json' -d @examples/prometheus.json
```

## How to filter out alerts
```
/label add service ibis
```
This will make that only warnings that contain _label_ `service=ibis` would be displayed
```
/label remove service ibis
```

## White list users that can receive alerts
comma separated list of Wire `usernames` in `whitelist` in the `config`.
Leave the list empty if you want to allow everybody to join.            