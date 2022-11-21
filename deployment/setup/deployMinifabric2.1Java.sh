#!/bin/bash

echo 'Make sure your firewall is disabled and you changed all necessary files !'

echo -e 'Cleanup...\n'
./minifab cleanup
echo -e '\nCleaned up!'

echo -e 'Checking all images\n'

# fabric-baseimage
docker pull chinyati/fabric-baseimage:arm64-0.4.20
docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage:arm64-0.4.20
docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage

# fabric-baseos
docker pull laughingadversial/fabric-baseos:3.0
docker tag  laughingadversial/fabric-baseos:3.0 hyperledger/fabric-baseos:arm64-0.4.20
docker tag  laughingadversial/fabric-baseos:3.0 hyperledger/fabric-baseos

# fabric-tools
docker pull laughingadversial/fabric-tools:3.0
docker tag laughingadversial/fabric-tools:3.0 hyperledger/fabric-tools:2.1
docker tag laughingadversial/fabric-tools:3.0 hyperledger/fabric-tools

# orderer
docker pull laughingadversial/fabric-orderer:3.0
docker tag laughingadversial/fabric-orderer:3.0 hyperledger/fabric-orderer:2.1
docker tag laughingadversial/fabric-orderer:3.0 hyperledger/fabric-orderer

# peer
docker pull laughingadversial/fabric-peer:3.0
docker tag laughingadversial/fabric-peer:3.0 hyperledger/fabric-peer:2.1
docker tag laughingadversial/fabric-peer:3.0 hyperledger/fabric-peer

# fabric-ccenv
docker pull laughingadversial/fabric-ccenv:3.0
docker tag laughingadversial/fabric-ccenv:3.0 hyperledger/fabric-ccenv:2.1
docker tag laughingadversial/fabric-ccenv:3.0 hyperledger/fabric-ccenv

# fabric-ca
docker pull couchdb:3.2.2
docker tag couchdb:3.2.2 hyperledger/fabric-ca:1.4
docker tag couchdb:3.2.2 hyperledger/fabric-ca

# couchdb
docker pull chinyati/fabric-couchdb:arm64-0.4.20
docker tag chinyati/fabric-couchdb:arm64-0.4.20 hyperledger/fabric-couchdb:arm64-0.4.20
docker tag chinyati/fabric-couchdb:arm64-0.4.20 hyperledger/fabric-couchdb

# javaenv
docker pull btl5037/fabric-javaenv:2.2.0-arm64
docker tag btl5037/fabric-javaenv:2.2.0-arm64 hyperledger/fabric-javaenv:2.1

#Node und go env muss ich noch schauen 

docker build -t hyperledgerlabs/minifab:latest .


echo -e'\nStarting minifabric 2.1\n'
./minifab netup -e true -i 2.1

echo -e  '\nInstalling jq...\n'
docker exec -it mysite apt-get update
docker exec -it mysite apt-get --assume-yes install jq

echo -e '\nNetwork status should work now\n'
./minifab stats

echo -e '\nCreating the channel\n'
./minifab create
./minifab join
./minifab anchorupdate
./minifab profilegen

echo -e '\nInstalling all java chaincodes\n'
./minifab install -l java
./minifab approve
./minifab commit
./minifab  initialize

echo -e '\nChaincode successfully installed!\n'

./minifab discover 

echo 'Setup finished'
