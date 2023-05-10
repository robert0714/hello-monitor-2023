# BUILD STAGE
# from redhat website: https://catalog.redhat.com/software/containers/ubi8/openjdk-11/5dd6a4b45a13461646f677f4?container-tabs=gti&gti-tabs=unauthenticated
# FROM registry.access.redhat.com/ubi8/openjdk-11
FROM registry.access.redhat.com/ubi8/openjdk-8:latest
WORKDIR /app

COPY ./target/microprofile-metrics-bootable.jar /app/microprofile-metrics-bootable.jar
 
# 8080 for services 、9990 for web console  
EXPOSE 8080/tcp 9990/tcp 

ENV TZ=Asia/Taipei
ENV JAVA_OPTS=""

# from wilfly to-do backend app, https://github.com/wildfly/quickstart/tree/main/microprofile-metrics

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS     -jar  /app/microprofile-metrics-bootable.jar"]