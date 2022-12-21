# Setup for Minifabric

In this directory install.sh is going to be the skript that will pull, all necessary images and dependencies. It initializes the steps towards utilizing this repository for debian based systems.

Testing of the functionality of the python replacement skript:
Cloning the feature branch on a Raspberry Pi 4 with Raspberry OS 64 bit. There I execute the skript in ```overwriteImagesInSubmodule.py``` to change the directory ```/deployment/minifabric```.Thus, I build the minifabric image from there with my current images in docker hub of laughingadversial. Then, the setup of minifabric is going to take place and I execute ```imagesMinifabricARM.sh``` as well as ```deployMinifabric2.2Java.sh``` from within ```/deployment/minifabric```.
