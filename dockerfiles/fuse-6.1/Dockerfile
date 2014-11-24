FROM dockerfile/java:oracle-java7

RUN mkdir /fuse
ADD target/jboss-fuse-6.1.0.redhat-379 /opt/jboss-fuse-6.1.0.redhat-379

CMD ["/opt/jboss-fuse-6.1.0.redhat-379/bin/fuse", "server"]
