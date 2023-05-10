FROM registry.access.redhat.com/ubi8/openjdk-8:latest

COPY ./target/weather-app-cloud-ready-1.0-bootable.jar  /app/weather-app-cloud-ready-1.0-bootable.jar
WORKDIR /app
 

EXPOSE 8080/tcp 9990/tcp
ENV TZ=Asia/Taipei
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS     -jar  /app/weather-app-cloud-ready-1.0-bootable.jar"]