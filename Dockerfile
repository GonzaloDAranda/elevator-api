FROM amazoncorretto:17
LABEL authors="gonzalo.aranda"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]