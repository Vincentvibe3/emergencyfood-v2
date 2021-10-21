package me.vincentvibe3.emergencyfood.core

import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.RequestHandler

/* enum representing environments
*  in which the bot may run */
enum class Channel {
    BETA, STABLE, LOCAL
}

fun main() {
    val channel = Channel.STABLE
    RequestHandler.get("https://fuel.gitbook.io/documentation/support/fuel-coroutines")
    Logging.logger.info("Setting up bot on channel $channel...")
    //setup bot
    Bot.setup(channel)
    Logging.logger.info("Starting bot...")
    //start bot
    Bot.start()


}

