package me.vincentvibe3.emergencyfood.core

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.utils.MessageCommandManager
import me.vincentvibe3.emergencyfood.utils.SlashCommandManager
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import me.vincentvibe3.emergencyfood.utils.events.ButtonsListener
import me.vincentvibe3.emergencyfood.utils.events.ReadyListener
import me.vincentvibe3.emergencyfood.utils.events.SlashCommandListener
import me.vincentvibe3.emergencyfood.utils.events.VoiceStateListener
import me.vincentvibe3.emergencyfood.utils.events.MessageListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity

object Bot {

    //use annotation to mark commands as beta to not register on stable channels
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Beta

    private lateinit var token:String
    private lateinit var client:JDA
    private lateinit var channel: Channel

    //set up the bot with token
    fun setup(channelValue: Channel){
        channel = channelValue
        when (channel) {
            Channel.STABLE -> {
                token = System.getenv("TOKEN")
            }
            Channel.BETA -> {
                token = System.getenv("TOKENBETA")
            }
            Channel.LOCAL -> {
                token = System.getenv("TOKEN")
            }
        }
    }

    //get the running bot client
    fun getClientInstance():JDA{
        return client
    }

    //start the bot
    fun start(){
        val activity = Activity.playing("Now using Slash Commands")
        client = JDABuilder.createDefault(token)
            .setActivity(activity)
            .addEventListeners(ReadyListener)
            .build()
        client.awaitReady()
        SlashCommandManager.registerRemote(channel)
        SlashCommandManager.registerGuildRemote(channel)
        MessageCommandManager
        runBlocking {
            launch {
                //run background check loops here
                PlayerManager.startCleanupLoop()
            }
            client.addEventListener(SlashCommandListener)
            client.addEventListener(ButtonsListener)
            client.addEventListener(VoiceStateListener)
            client.addEventListener(MessageListener)
        }


    }
}