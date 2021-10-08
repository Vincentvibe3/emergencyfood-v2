package me.vincentvibe3.emergencyfood.core

/* enum representing environments
*  in which the bot may run */
enum class Channel {
    BETA, STABLE, LOCAL
}

fun main() {
    val channel = Channel.BETA
    println("Setting up bot on channel $channel...")
    //setup bot
    Bot.setup(channel)
    println("Starting bot...")
    //start bot
    Bot.start()


}

