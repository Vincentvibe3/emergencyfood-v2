# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre
WORKDIR /home/current
COPY /build/libs/Emergencyfood-*.jar ./bot.jar
CMD ["java", "-jar", "bot.jar"]
