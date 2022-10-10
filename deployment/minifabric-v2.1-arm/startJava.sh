#!/bin/bash

echo 'Make sure your firewall is disabled and you changed all necessary files !'

echo 'Cleanup...'
./minifab cleanup
echo 'Cleaned up!'

echo 'Checking all images'

# fabric-baseimage
docker pull chinyati/fabric-baseimage:arm64-0.4.20
docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage:arm64-0.4.20
docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage

# fabric-baseos
docker pull chinyati/fabric-baseos:arm64-0.4.20
docker tag chinyati/fabric-baseos:arm64-0.4.20 hyperledger/fabric-baseos:arm64-0.4.20
docker tag chinyati/fabric-baseos:arm64-0.4.20 hyperledger/fabric-baseos

# fabric-tools
docker pull chinyati/fabric-tools:arm64-2.1.0
docker tag chinyati/fabric-tools:arm64-2.1.0 hyperledger/fabric-tools:2.1
docker tag chinyati/fabric-tools:arm64-2.1.0 hyperledger/fabric-tools

# orderer
docker pull chinyati/fabric-orderer:arm64-2.1.0
docker tag chinyati/fabric-orderer:arm64-2.1.0 hyperledger/fabric-orderer:2.1
docker tag chinyati/fabric-orderer:arm64-2.1.0 hyperledger/fabric-orderer

# peer
docker pull chinyati/fabric-peer:arm64-2.1.0
docker tag chinyati/fabric-peer:arm64-2.1.0 hyperledger/fabric-peer:2.1
docker tag chinyati/fabric-peer:arm64-2.1.0 hyperledger/fabric-peer

# fabric-ccenv
docker pull chinyati/fabric-ccenv:arm64-2.1.0
docker tag chinyati/fabric-ccenv:arm64-2.1.0 hyperledger/fabric-ccenv:2.1
docker tag chinyati/fabric-ccenv:arm64-2.1.0 hyperledger/fabric-ccenv

# fabric-ca
docker pull chinyati/fabric-ca:arm64-1.4.7
docker tag chinyati/fabric-ca:arm64-1.4.7 hyperledger/fabric-ca:1.4
docker tag chinyati/fabric-ca:arm64-1.4.7 hyperledger/fabric-ca

# couchdb
docker pull chinyati/fabric-couchdb:arm64-0.4.20
docker tag chinyati/fabric-couchdb:arm64-0.4.20 hyperledger/fabric-couchdb:arm64-0.4.20
docker tag chinyati/fabric-couchdb:arm64-0.4.20 hyperledger/fabric-couchdb

# javaenv
docker pull btl5037/fabric-javaenv:2.2.0-arm64
docker tag btl5037/fabric-javaenv:2.2.0-arm64 hyperledger/fabric-javaenv:2.1

#Node und go ênvmuss ich noch schauen 

docker build -t hyperledgerlabs/minifab:latest .


echo 'Starting minifabric 2.1'
./minifab netup -e true -i 2.1

echo 'Installing jq...'
docker exec -it mysite apt-get update
docker exec -it mysite apt-get --assume-yes install jq

echo 'Network status should work now'
./minifab stats

echo 'Creating the channel'
./minifab create
./minifab join
./minifab anchorupdate
./minifab profilegen

echo 'Installing all java chaincodes'
./minifab install -l java
./minifab approve
./minifab commit
./minifab  initialize
echo 'Chaincode successfully installed!'

./minifab discover 

echo 'Setup finished'
