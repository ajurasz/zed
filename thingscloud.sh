#!/bin/sh

docker run -e DOCKER_HOST=tcp://172.16.42.1:2375 -p 8080:8080 -p 15005:15005 -it hekonsek/zedpanel:0.0.17