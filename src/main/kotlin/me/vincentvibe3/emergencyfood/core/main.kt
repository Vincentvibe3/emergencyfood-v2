package me.vincentvibe3.emergencyfood.core

import me.vincentvibe3.emergencyfood.internals.CommandManager
import me.vincentvibe3.emergencyfood.internals.ConfigLoader
import me.vincentvibe3.emergencyfood.internals.ConfigLoader.Channel
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.Templates

fun main() {
    val channel = Channel.BETA
    Logging.logger.info("Setting up bot on channel $channel...")
    ConfigLoader.load()
    CommandManager
    Logging.logger.info("Setting up custom rate limits...")
    Templates.setRateLimits()
    Logging.logger.info("Starting bot...")

    //start bot
    Bot.start()


}

