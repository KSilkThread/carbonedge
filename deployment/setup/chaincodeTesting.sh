#!/bin/bash
echo -e '\nInstalling all java chaincodes\n'
./minifab install -l java
./minifab approve
./minifab commit
./minifab  initialize

echo -e '\nChaincode successfully installed!\n'

./minifab discover 