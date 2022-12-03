#!/bin/bash

echo 'Starting minifabric 2.2 on ARM'
echo 'Shutdown existing nodes!'

./minifab down

echo 'Start network'

./minifab netup

echo 'Installing JQ'

docker exec -it mysite apt-get update
docker exec -it mysite apt-get --assume-yes install jq

echo 'Check stats'

./minifab stats

echo 'Starting procedure completed'