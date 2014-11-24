#!/bin/sh

rm -rf target
mkdir target
unzip /home/hekonsek/labs/jboss-fuse-full-6.1.0.redhat-379.zip -d target/

docker build -t zeddocker/fuse61 .