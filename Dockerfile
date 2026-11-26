FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn/ .mvn/
COPY src/ src/
RUN ./mvnw --batch-mode --no-transfer-progress package -DskipTests

FROM tomcat:11.0.29-jre21-temurin-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/Foundly-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/Foundly.war
