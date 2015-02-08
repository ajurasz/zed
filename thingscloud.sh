#!/bin/sh

DOCKER_HOST = `ip route | awk '/docker/ { print $NF }'`
docker run -e DOCKERHOST=tcp://${DOCKER_HOST}:2375 -p 8080:8080 -p 15005:15005 -it hekonsek/zedpanel:0.0.17