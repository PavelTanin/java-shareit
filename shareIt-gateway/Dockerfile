FROM amazoncorretto:11-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","${JAVA_OPTS}","-jar","/app.jar"]
