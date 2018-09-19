# Alert-bot
This is alert bot for Wire.

## How to send alerts
```
curl 'http://localhost:8080/alert/broadcast -H'secret:$SECRET' -H'content-type:application/json'-d \
    '{ "message" : "Hi There!", "environment":"prod", "service":"ibis" }'
```

## White list users that can receive alerts
comma separated list of Wire `usernames` in `whitelist` in the `config`.
Leave the list empty if you want to allow everybody to join.