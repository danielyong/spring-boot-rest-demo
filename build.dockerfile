FROM maven:3.9-eclipse-temurin-21 as build
WORKDIR /home
ADD . .
RUN [ "mvn", "clean", "package", "-Dmaven.test.skip=true" ]
FROM eclipse-temurin:21-jre-alpine
WORKDIR /home
COPY --from=build /home/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "./app.jar"]