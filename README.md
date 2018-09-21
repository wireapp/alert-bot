# Alert-bot
This is alert bot for Wire.

## How to send alerts
```
curl 'localhost:8080/alert/prometheus' -H "Authorization:Bearer $TOKEN" -H'content-type:application/json' -d @examples/prometheus.json
```

## How to filter out alerts
```
/annotation add service ibis
```
This will make that only warnings that contain _annotation_ `service=ibis` would be displayed
```
/annotation remove service ibis
```

## White list users that can receive alerts
comma separated list of Wire `usernames` in `whitelist` in the `config`.
Leave the list empty if you want to allow everybody to join.            