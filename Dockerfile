FROM openjdk:17-jdk-slim

WORKDIR /app

COPY gradle/wrapper/ gradle/wrapper/
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY src/ src/

RUN ./gradlew build -x test

EXPOSE 8086

CMD ["java", "-jar", "build/libs/tukio-notification-service-0.0.1-SNAPSHOT.jar"]
