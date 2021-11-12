# emergencyfood-v2

A rewrite of the emergencyfood bot in Kotlin

Looking for the original project? Find it [here](https://github.com/Vincentvibe3/EmergencyFoodBot).

## Table of Contents

* [Status](#Status)
* [Dependencies](#Dependencies)
* [Configuration](#Configuration)
  * [botConfig.json](#*botConfig.json*)
  * [config.bot.kts](#*config.bot.kts*)
  * [Environment Variables](#*Environment Variables*)
* [Self Hosting](#Self-Host)

## Status

*Main branch:* 

![Build Status Github Actions](https://github.com/Vincentvibe3/emergencyfood-v2/actions/workflows/Build.yaml/badge.svg?branch=main) 
 
*Version 1.4.2:*

[![Release Status](https://dev.azure.com/vincentvibe4/emergencyfood/_apis/build/status/Vincentvibe3.emergencyfood-v2?branchName=refs%2Ftags%2Fv.1.4.2)](https://dev.azure.com/vincentvibe4/emergencyfood/_build/latest?definitionId=5&branchName=refs%2Ftags%2Fv.1.4.2)

## Dependencies

This project is made possible by:

- [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA)
- [Lavaplayer](https://github.com/sedmelluq/lavaplayer)
- [Ktor](https://ktor.io/)
- [Jsoup](https://jsoup.org/)
- [JSON](https://github.com/stleary/JSON-java)

## Configuration

Configuration is possible through 3 methods

1. A `botConfig.json` file
2. A `config.bot.kts` file
3. Environment Variables

The bot will attempt to load a config from each of these methods in this order.
Failure to load any config will prevent the bot from launching. 

### *botConfig.json*

An example can be found [here]()

### *config.bot.kts*

>**⚠ Warning! ⚠**
> 
> This method will evaluate the script when loading the config.
> Make sure that you verify that `config.bot.kts` is safe before starting the bot.

An example can be found [here]()

### *Environment Variables*

> **Note:**
>
> The following method only allows for more basic configurations than the other methods.
> Consider using `botConfig.json` or `config,bot.kts` if you need more control over the configuration.


The following environment variables are **required**:

`TOKEN`: This is the token used when the bot launches on a stable channel

`TOKEN_BETA`: This is the token used when the bot launches on a stable channel

`BOT_OWNER`: This is the [id](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-) of the discord user owning the bot and is used for the admin commands

The following environment variables are **optional**:

`CHANNEL`: This represents the channel on which the bot should be running. 
It's value may only be `STABLE`, `BETA` or `LOCAL`. 
If this is missing or does not match a valid value the bot will default to `STABLE`

## Self-Host
### Grab a release

Download a release from the [releases](https://github.com/Vincentvibe3/emergencyfood-v2/releases/latest) page

### Build from source
1. Clone the repository with ```git clone https://github.com/Vincentvibe3/emergencyfood-v2.git```

2. Build from source with ```./gradlew build```

3. The produced binary will be under ```build/libs/Emergencyfood-<version>.jar```

### Running the bot
Use ```java -jar <Location of the jar>``` to start the bot