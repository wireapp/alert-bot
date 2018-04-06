# Alert-bot
This is alert bot for Wire.

## How to send alerts
curl -i -XPOST http://localhost:8081/alert -d '{ "message" : "Hi There!" }' -H'content-type:application/json'
