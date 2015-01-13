#!/bin/sh

(cd fatjar; ./build.sh) &&
(cd zed-document-service-mongo; ./build.sh)