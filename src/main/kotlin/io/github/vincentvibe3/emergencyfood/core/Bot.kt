package io.github.vincentvibe3.emergencyfood.core

import io.github.vincentvibe3.emergencyfood.internals.CommandManager
import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.internals.UniversalListener
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import io.github.vincentvibe3.emergencyfood.utils.nameroulette.NamerouletteEventLoop
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

object Bot {

    private lateinit var client:JDA

    //get the running bot client
    fun getClientInstance(): JDA {
        return client
    }

    private fun buildClient(): JDA {
        val activity = Activity.playing(Config.status)
        val builder = JDABuilder.createLight(Config.token)
        return builder
            .enableCache(CacheFlag.VOICE_STATE)
            .setChunkingFilter(ChunkingFilter.NONE)
            .setLargeThreshold(50)
            .setMemberCachePolicy(
                MemberCachePolicy.VOICE
                    .or(MemberCachePolicy.OWNER)
            )
            .enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
            )
            .setActivity(activity)
            .addEventListeners(UniversalListener)
            .build()
    }

    //start the bot
    fun start() {
        try {
            client = buildClient()
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