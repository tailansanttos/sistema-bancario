FROM amazoncorretto:21
VOLUME /tmp
ARG JAR_FILE=target/banco-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
