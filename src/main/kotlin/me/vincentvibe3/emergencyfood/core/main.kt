package me.vincentvibe3.emergencyfood.core

import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.audio.SongSearch

/* enum representing environments
*  in which the bot may run */
enum class Channel {
    BETA, STABLE, LOCAL
}

fun main() {
    val channel = Channel.BETA
    Logging.logger.info("Setting up bot on channel $channel...")
    //setup bot
    Bot.setup(channel)
    Logging.logger.info("Starting bot...")

    //start bot
    Bot.start()


}

