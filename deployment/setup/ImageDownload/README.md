# Setup Process

## Remote Setup

The setup process from admin perspective... starting a ansible playbook

## Node Setup

The setup process from node perspective... tasks which are invoked by the admin's ansible playbook

### Operator Node

### Validator Node

### Reporting Organization Node

## Image Download

The directory is determined to pull the correct images from docker for the usage of Minifabric on ARM64 or other architectures.

Therefore a replacement in Minifabric Submodul is written for the correct build process of the necessary image.

Testing the functionality of the python replacement skript:
Cloning the feature branch on a Raspberry Pi 4 with Raspberry OS 64 bit. There I executed the skript ```overwriteImagesInSubmodule.py``` to change the directory ```/deployment/minifabric```.Thus, I built the minifabric image from there with my current images in docker hub of laughingadversial with ```sudo docker build -t hyperledgerlabs/minifab:latest .```. Then, the setup of minifabric is going to take place and I execute ```imagesMinifabricARM.sh``` as well as ```deployMinifabric2.2Java.sh``` from within ```/deployment/minifabric```.
