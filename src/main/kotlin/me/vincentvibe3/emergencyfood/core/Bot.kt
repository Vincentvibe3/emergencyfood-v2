package me.vincentvibe3.emergencyfood.core

import me.vincentvibe3.emergencyfood.utils.SlashCommandManager
import me.vincentvibe3.emergencyfood.utils.events.ButtonsListener
import me.vincentvibe3.emergencyfood.utils.events.ReadyListener
import me.vincentvibe3.emergencyfood.utils.events.SlashCommandListener
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
        client.addEventListener(SlashCommandListener)
        client.addEventListener(ButtonsListener)
    }
}