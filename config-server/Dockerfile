FROM eclipse-temurin:17-jdk-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY target/config-server-1.0.0.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]