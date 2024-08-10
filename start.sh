#!/bin/sh
#apk upgrade -U --no-cache yt-dlp
. ./pyenv/bin/activate
pip install -U yt-dlp
yt-dlp --version
java -jar bot.jar