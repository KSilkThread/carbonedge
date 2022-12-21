#!/bin/bash

# Clone the repository
git clone https://github.com/LaughingAdversial/fabric

# Navigate to the repository directory
cd fabric

# Install the repository using any installation instructions provided in the documentation
# For example, if the repository includes a package.json file:
# npm install # i need a dependencies json therefore called package.json
echo "installing dependencies "

# npm install  ./setup_for_v2.2/package.json

echo -e "installing dependencies finished\n" 

echo "Creating new environment and installing modules for python "

sudo apt-get install python3-pip
pip install --upgrade pip
python3 -m venv minifabric
source minifabric/bin/activate
#pip install Jinja2

echo -e "\ndownload of necessary images\n"

#donwloading carbon tracking images 
./setup/ImageDownload/imagesMinifabricCarbon.sh

ARCH=$(shell go env GOARCH)

if [ "$ARCH" == "arm64" ]; then
    #download images refering to amd or arm
    ./setup/ImageDownload/imagesMinifabricARM.sh
    ./setup/ImageDownload/overwriteImagesInSubmodule.py
else
  echo "no specific adjustment needed"
fi

MARCH=$(shell go env GOOS)-$(shell go env GOARCH)





# inputs needed for clarification of the needed nodes etc. for the different roles 
# of farmer, controller, reporter



# input


#Here should be executed the minifab skript 
# dont forget to maybe add: sudo chmod 666 /var/run/docker.sock 
#chmod +x script.sh
#./script.sh
