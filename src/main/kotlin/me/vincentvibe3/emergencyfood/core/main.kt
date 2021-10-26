package me.vincentvibe3.emergencyfood.core

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.RequestHandler

/* enum representing environments
*  in which the bot may run */
enum class Channel {
    BETA, STABLE, LOCAL
}

fun main() {
    val channel = Channel.STABLE
    runBlocking {
        for (i in 1..100) {
            launch {
                println("${RequestHandler.get("http://127.0.0.1:8000", i)} $i")
                println("launched")
            }
            println("collected")
        }
    }

    Logging.logger.info("Setting up bot on channel $channel...")
    //setup bot
    Bot.setup(channel)
    Logging.logger.info("Starting bot...")
    //start bot
    Bot.start()


}

