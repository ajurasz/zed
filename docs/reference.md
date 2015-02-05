# Zed platform reference documentation

## Services

### Document Service

Document service provides storage and query facilities for documents. By document we understand a hierarchical, JSON-like data.

#### MongoDB Document Service

The default implementation of document service is based on MongoDB. We call it MongoDB Document Service.

##### Deploying MongoDB Document Service

There are several options to deploy and run MongoDB Document Service.

###### Deploying MongoDB Document Service using fat WAR

The easiest way to deploy MongoDB Document Service is to the [download `zed-service-document-mongodb`
fat WAR](http://search.maven.org/remotecontent?filepath=com/github/zed-platform/zed-service-document-mongodb/0.0.16/zed-service-document-mongodb-0.0.16.war)
and start it using a command line:

    java -jar zed-service-document-mongodb-0.0.16.war

The command above will start MongoDB Document Service REST API on default port 15001 (and Jolokia API on default
port 15000).

###### Deploying MongoDB Document Service using Zed shell fat jar deployer

You can also download MongoDB Document Service using Zed shell and fat jar deployer:

    deploy fatjar:mvn:com.github.zed-platform/zed-service-document-mongodb/0.0.16/zed-service-document-mongodb/war

The command above will download MongoDB Document Service fat WAR into the `default` workspace
`~/.zed/deploy/default/zed-service-document-mongodb-0.0.16.war`.

###### Deploying MongoDB Document Service using Docker client

You can also run MongoDB Document Service using Docker image available in the
[Docker Hub](https://registry.hub.docker.com/u/hekonsek/zed-service-document-mongodb):

    docker run 15000:15000 -p 15001:15001 -it hekonsek/zed-service-document-mongodb:0.0.16

The command above pulls service image from the Docker Hub and starts MongoDB Document Service REST API on default port
15001 (and Jolokia API on default port 15000).