package me.vincentvibe3.emergencyfood.core

import kotlinx.coroutines.delay
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
    val channel = Channel.BETA
//    runBlocking {
//        var i = 0
//        val start = System.currentTimeMillis()
//        launch {
//            repeat(100) {
//                launch {
//                    println("${RequestHandler.get("http://127.0.0.1:8000", it)} $it")
//                    println("launched")
//                }
//                println("collected")
//            }
//        }.join()
//        var end = System.currentTimeMillis()
//        println("${end-start}")
//    }

    Logging.logger.info("Setting up bot on channel $channel...")
    //setup bot
    Bot.setup(channel)
    Logging.logger.info("Starting bot...")
    //start bot
    Bot.start()


}

