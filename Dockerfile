# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre-alpine
RUN #apk add --update --no-cache yt-dlp
RUN apk add --update --no-cache python3 py3-pip
WORKDIR /home/current
COPY /build/libs/Emergencyfood-*.jar ./bot.jar
COPY /start.sh ./start.sh
RUN chmod +x ./start.sh
RUN python3 -m venv ./pyenv
ENTRYPOINT ["./start.sh"]
