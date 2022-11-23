#!/bin/bash

echo 'Make sure your firewall is disabled and you changed all necessary files !'

echo -e 'Cleanup...\n'
./minifab cleanup
echo -e '\nCleaned up!'

echo -e 'Checking all images\n'

# fabric-baseimage not done yet is this needed anylonger
#docker pull chinyati/fabric-baseimage:arm64-0.4.20
#docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage:arm64-0.4.20
#docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage

# fabric-baseos --> peters
docker pull laughingadversial/fabric-baseos:3.0
docker tag  laughingadversial/fabric-baseos:3.0 hyperledger/fabric-baseos:arm64-0.4.20
docker tag  laughingadversial/fabric-baseos:3.0 hyperledger/fabric-baseos

# fabric-tools --> peters
docker pull laughingadversial/fabric-tools:3.0
docker tag laughingadversial/fabric-tools:3.0 hyperledger/fabric-tools:2.1
docker tag laughingadversial/fabric-tools:3.0 hyperledger/fabric-tools

# orderer --> peters
docker pull laughingadversial/fabric-orderer:3.0
docker tag laughingadversial/fabric-orderer:3.0 hyperledger/fabric-orderer:2.1
docker tag laughingadversial/fabric-orderer:3.0 hyperledger/fabric-orderer

# peer --> peters
docker pull laughingadversial/fabric-peer:3.0
docker tag laughingadversial/fabric-peer:3.0 hyperledger/fabric-peer:2.1
docker tag laughingadversial/fabric-peer:3.0 hyperledger/fabric-peer

# fabric-ccenv --> peters
docker pull laughingadversial/fabric-ccenv:3.0
docker tag laughingadversial/fabric-ccenv:3.0 hyperledger/fabric-ccenv:2.1
docker tag laughingadversial/fabric-ccenv:3.0 hyperledger/fabric-ccenv

# fabric-ca --> peters
docker pull laughingadversial/fabric-ca:arm64-1.5.5
docker tag laughingadversial/fabric-ca:arm64-1.5.5 hyperledger/fabric-ca:1.4
docker tag laughingadversial/fabric-ca:arm64-1.5.5 hyperledger/fabric-ca

# couchdb --> original couchdb
docker pull couchdb:3.2.2
docker tag couchdb:3.2.2 hyperledger/fabric-couchdb:arm64-0.4.20
docker tag couchdb:3.2.2 hyperledger/fabric-couchdb

# javaenv --> peters
docker pull laughingadversial/fabric-javaenv:arm64-2.5.0
docker tag laughingadversial/fabric-javaenv:arm64-2.5.0 hyperledger/fabric-javaenv:2.1
docker tag laughingadversial/fabric-javaenv:arm64-2.5.0 hyperledger/fabric-javaenv:3.0

#Node und go env muss ich noch schauen 

#sudo docker build -t hyperledgerlabs/minifab:latest .


echo -e '\nStarting minifabric 2.1\n'
./minifab netup -e true -i 2.1

#echo -e  '\nInstalling jq...\n' does not install during skript execution but everything is functioning
#docker exec -it mysite apt-get update
#docker exec -it mysite apt-get --assume-yes install jq

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
