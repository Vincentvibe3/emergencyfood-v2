package me.vincentvibe3.emergencyfood.core

import me.vincentvibe3.emergencyfood.utils.Logging

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
    Logging.logger.info("Starting bot...")

    //start bot
    Bot.start()


}

