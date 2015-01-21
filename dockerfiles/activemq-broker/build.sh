#!/bin/sh

docker build -t hekonsek/activemq-broker:0.0.14-SNAPSHOT .
docker push hekonsek/activemq-broker:0.0.14-SNAPSHOT