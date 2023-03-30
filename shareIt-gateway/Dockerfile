FROM amazoncorretto:11-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar -Dserver.port=8080 /app.jar"]
