#!/bin/bash
echo 'Make sure your firewall is disabled and you changed all necessary files !'

echo -e 'Cleanup...\n'
./minifab cleanup
echo -e '\nCleaned up!'

echo -e 'Checking all images\n'

#is this needed anylonger?
# fabric-baseimage not done yet
#docker pull chinyati/fabric-baseimage:arm64-0.4.20
#docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage:arm64-0.4.20
#docker tag chinyati/fabric-baseimage:arm64-0.4.20 hyperledger/fabric-baseimage

# fabric-baseos --> peters
docker pull laughingadversial/fabric-baseos:3.0
docker tag  laughingadversial/fabric-baseos:3.0 hyperledger/fabric-baseos:arm64-0.4.20
docker tag  laughingadversial/fabric-baseos:3.0 hyperledger/fabric-baseos

# fabric-tools --> peters
docker pull laughingadversial/fabric-tools:3.0
docker tag laughingadversial/fabric-tools:3.0 hyperledger/fabric-tools:2.2
docker tag laughingadversial/fabric-tools:3.0 hyperledger/fabric-tools

# orderer --> peters
docker pull laughingadversial/fabric-orderer:3.0
docker tag laughingadversial/fabric-orderer:3.0 hyperledger/fabric-orderer:2.2
docker tag laughingadversial/fabric-orderer:3.0 hyperledger/fabric-orderer

# peer --> peters
docker pull laughingadversial/fabric-peer:3.0
docker tag laughingadversial/fabric-peer:3.0 hyperledger/fabric-peer:2.2
docker tag laughingadversial/fabric-peer:3.0 hyperledger/fabric-peer

# fabric-ccenv --> peters
docker pull laughingadversial/fabric-ccenv:3.0
docker tag laughingadversial/fabric-ccenv:3.0 hyperledger/fabric-ccenv:2.2
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
docker tag laughingadversial/fabric-javaenv:arm64-2.5.0 hyperledger/fabric-javaenv:2.2
docker tag laughingadversial/fabric-javaenv:arm64-2.5.0 hyperledger/fabric-javaenv:3.0

#Node und go env muss ich noch schauen 

sudo docker build -t hyperledgerlabs/minifab:latest .
