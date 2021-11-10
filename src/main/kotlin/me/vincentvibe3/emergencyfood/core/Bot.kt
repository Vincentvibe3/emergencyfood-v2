package me.vincentvibe3.emergencyfood.core

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.internals.CommandManager
import me.vincentvibe3.emergencyfood.internals.ConfigLoader
import me.vincentvibe3.emergencyfood.internals.ConfigLoader.Channel
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import me.vincentvibe3.emergencyfood.internals.events.*
import me.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

object Bot {

    //use annotation to mark commands as beta to not register on stable channels
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Beta

    private lateinit var client:JDA

    //get the running bot client
    fun getClientInstance():JDA{
        return client
    }

    //start the bot
    fun start(){
        val activity = Activity.playing("Now using Slash Commands")
        try {
            client = JDABuilder.createDefault(ConfigLoader.token)
                .setActivity(activity)
                .addEventListeners(ReadyListener)
                .build()
        } catch (e:LoginException){
            Logging.logger.error("Invalid Token was passed")
            exitProcess(1)
        }
        client.awaitReady()
        CommandManager.registerRemote(ConfigLoader.channel)
        CommandManager.registerGuildRemote(ConfigLoader.channel)
        runBlocking {
            launch {
                //run background check loops here
                PlayerManager.startCleanupLoop()
            }
            client.addEventListener(SlashCommandListener)
            client.addEventListener(ButtonsListener)
            client.addEventListener(MessageListener)
            client.addEventListener(VoiceStateListener)
        }
    }
}