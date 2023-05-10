# FINAL STAGE
# FROM  quay.io/eduk8s/jdk8-environment:latest # for Debug  
# FROM adoptopenjdk/openjdk8:alpine
# FROM adoptopenjdk/openjdk8:x86_64-ubi-jdk8u342-b07
FROM registry.access.redhat.com/ubi8/openjdk-8:latest

COPY ./target/helloworld-mdb-bootable.jar  /app/helloworld-mdb-bootable.jar
WORKDIR /app


EXPOSE 8080/tcp 9990/tcp
ENV TZ=Asia/Taipei
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS     -jar  /app/helloworld-mdb-bootable.jar"]