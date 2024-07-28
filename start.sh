#!/bin/sh
#apk upgrade -U --no-cache yt-dlp
ls pyenv
. ./pyenv/bin/activate
pip install -U yt-dlp
yt-dlp --version
java -jar bot.jar