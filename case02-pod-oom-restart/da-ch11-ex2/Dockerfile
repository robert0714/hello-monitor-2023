FROM registry.access.redhat.com/ubi8/openjdk-8:latest

COPY ./target/da-ch11-ex2-1.0-SNAPSHOT.jar  /app/da-ch11-ex2.jar
WORKDIR /app
 

EXPOSE 8080/tcp 9990/tcp
ENV TZ=Asia/Taipei
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -cp  /app/da-ch11-ex2.jar main.Main"]