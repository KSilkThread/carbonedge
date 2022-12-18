#!/bin/bash

# Clone the repository
git clone https://github.com/LaughingAdversial/fabric

# Navigate to the repository directory
cd fabric

# Install the repository using any installation instructions provided in the documentation
# For example, if the repository includes a package.json file:
#npm install # i need a dependencies json therefore called package.json
echo "installing dependencies "

npm install  ../"setup for v2.2"/package.json

echo "installing dependencies finished" 
# yq got added now i need the repo for my minifabric and 
# adjust the serialization of the packages 
# download current minifabric from our repo or the original, then adjust and overwrite the 
# nessessary files 


echo "download of necessary images"

## Build of docker images ? sudo docker build -t hyperledgerlabs/minifab:latest .
# examin what system it is $AARCH == ARM64 or amd64 
# safe system related information 
ARCH=$(shell go env GOARCH)
MARCH=$(shell go env GOOS)-$(shell go env GOARCH)

#download images refering to amd or arm



# inputs needed for clarification of the needed nodes etc. for the different roles 
# of farmer, controller, reporter



# input


#Here should be executed the minifab skript 
# dont forget to maybe add: sudo chmod 666 /var/run/docker.sock 
#chmod +x script.sh
#./script.sh
