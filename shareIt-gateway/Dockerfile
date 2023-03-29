FROM amazoncorretto:11-alpine
VOLUME /creatingData
COPY target/*.jar app.jar
ENTRYPOINT ["java ${JAVA_OPTS} -jar /app.jar"]
