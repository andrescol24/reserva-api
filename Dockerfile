FROM openjdk:16.0.2-jdk
COPY ./target/reservas-api-1.0.0.jar app.jar
EXPOSE 4005
ENTRYPOINT ["java", "-jar", "app.jar"]