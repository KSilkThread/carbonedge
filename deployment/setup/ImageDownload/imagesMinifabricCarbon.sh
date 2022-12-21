#!/bin/bash

echo -e "Downloading necessary images for carbon tracking\n"

#All images described in deployment/docs

ARRAY=(nodered/node-red:latest grafana/grafana:latest eclipse-mosquitto:latest ksilkthread/carbonmeter-webapp:latest)

for element in ${ARRAY[@]}
do
    docker pull $element
done

echo -e "Download of necessary images for carbon tracking done\n"