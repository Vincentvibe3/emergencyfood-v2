package io.github.vincentvibe3.emergencyfood.core

import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.internals.ConfigLoader
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

