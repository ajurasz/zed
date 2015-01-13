#!/bin/sh

mvn -f ../../service/document/mongo/pom.xml clean install docker:build docker:push