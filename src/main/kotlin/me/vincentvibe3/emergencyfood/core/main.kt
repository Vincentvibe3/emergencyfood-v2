package me.vincentvibe3.emergencyfood.core

import me.vincentvibe3.emergencyfood.internals.CommandManager
import me.vincentvibe3.emergencyfood.internals.Config
import me.vincentvibe3.emergencyfood.internals.ConfigLoader
import me.vincentvibe3.emergencyfood.internals.ConfigLoader.Channel
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.Templates

fun main() {
    Logging.logger.info("Loading Config...")
    ConfigLoader.load()
    val channel = Config.channel
    Logging.logger.info("Setting up bot on channel $channel...")
    Logging.logger.info("Setting up custom rate limits...")
    Templates.setRateLimits()
    Logging.logger.info("Starting bot...")

    //start bot
    Bot.start()


}

