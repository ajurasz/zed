# Zed platform reference documentation

## Services

### Document Service

Document service provides storage and query facilities for documents. By document we understand a hierarchical, JSON-like data.

#### MongoDB document service

The default implementation of document service is based on MongoDB. The easiest way to deploy MongoDB-backed document service is to the [download `zed-service-document-mongo` fat WAR](http://search.maven.org/remotecontent?filepath=com/github/zed-platform/zed-service-document-mongo/0.0.8/zed-service-document-mongo-0.0.8.war) and start it using a command line:

    java -jar zed-service-document-mongo-0.0.8.war

The command above will start MongoDB-based document service REST API on default port 15001 (and Jolokia API on default port 15000).
