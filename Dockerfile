# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre
WORKDIR /home/current
COPY /build/libs/Emergencyfood-*.jar ./
CMD ["/bin/sh", "-c", "java -jar *.jar"]
