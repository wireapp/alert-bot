# Alert-bot
This is alert bot for Wire.

## How to send alerts
curl -i -XPOST http://localhost:8081/admin/alert -d '{ "message" : "Hi There!" }' -H'content-type:application/json'

## White list users that can receive alerts
comma separated list of Wire `usernames` in `whitelist` in the `config`.
Leave the list empty if you want to allow everybody to join.