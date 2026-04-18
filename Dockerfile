FROM maven AS Build
WORKDIR app/
COPY src/  ./
COPY pom.xml ./
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=Build /app/target/login-backend-0.0.1-SNAPSHOT.jar loginform.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "loginform.jar"]