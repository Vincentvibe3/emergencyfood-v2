package io.github.vincentvibe3.emergencyfood.core

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import io.github.vincentvibe3.emergencyfood.internals.CommandManager
import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import io.github.vincentvibe3.emergencyfood.internals.events.*
import io.github.vincentvibe3.emergencyfood.utils.NamerouletteEventLoop
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

object Bot {

    private lateinit var client:JDA

    //get the running bot client
    fun getClientInstance(): JDA {
        return client
    }

    //start the bot
    fun start() {
        val activity = Activity.playing(Config.status)
        try {
            client = JDABuilder.createDefault(Config.token)
                .enableIntents( GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(activity)
                .addEventListeners(UniversalListener)
                .build()
        } catch (e: LoginException) {
            Logging.logger.error("Invalid Token was passed")
            exitProcess(1)
        }
        client.awaitReady()
        CommandManager.registerRemote(Config.channel)
        CommandManager.registerGuildRemote(Config.channel)
        runBlocking {
            //run background check loops here
            launch {
                PlayerManager.startCleanupLoop()
            }
            launch {
                NamerouletteEventLoop.startLoop()
            }
        }
    }
}