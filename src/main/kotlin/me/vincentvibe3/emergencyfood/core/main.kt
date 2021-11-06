package me.vincentvibe3.emergencyfood.core

import io.ktor.http.*
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.Templates
import java.net.URLEncoder

/* enum representing environments
*  in which the bot may run */
enum class Channel {
    BETA, STABLE, LOCAL
}

fun main() {
    val channel = Channel.STABLE
    Logging.logger.info("Setting up bot on channel $channel...")
    //setup bot
    Bot.setup(channel)
    Logging.logger.info("Setting up custom rate limits...")
    Templates.setRateLimits()
    Logging.logger.info("Starting bot...")

    //start bot
    Bot.start()


}

