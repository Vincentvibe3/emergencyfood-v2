package me.vincentvibe3.emergencyfood.core

import io.ktor.http.*
import me.vincentvibe3.emergencyfood.internals.Config
import me.vincentvibe3.emergencyfood.internals.Config.Channel
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.Templates
import java.io.File
import java.net.URLEncoder

fun main() {
    val channel = Channel.STABLE
    Logging.logger.info("Setting up bot on channel $channel...")
    Config.load()
    //setup bot
    Bot.setup(channel)
    Logging.logger.info("Setting up custom rate limits...")
    Templates.setRateLimits()
    Logging.logger.info("Starting bot...")

    //start bot
    Bot.start()


}

