#!/bin/bash

echo -e '\nStarting minifabric 2.2\n'
./minifab netup -e true -i 2.2

#echo -e  '\nInstalling jq...\n'
#docker exec -it mysite apt-get update
#docker exec -it mysite apt-get --assume-yes install jq

echo -e '\nNetwork status should work now\n'
./minifab stats

echo -e '\nCreating the channel\n'
./minifab create
./minifab join
./minifab anchorupdate
./minifab profilegen