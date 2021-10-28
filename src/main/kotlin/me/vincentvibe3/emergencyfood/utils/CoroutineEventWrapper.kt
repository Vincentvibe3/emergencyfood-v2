package me.vincentvibe3.emergencyfood.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.events.MessageListener
import me.vincentvibe3.emergencyfood.utils.events.SlashCommandListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter

object CoroutineEventWrapper:EventListener {

    private val listeners = ArrayList<ListenerAdapter>()

    init {
        Bot.getClientInstance().addEventListener(this)
    }

    fun JDA.addCoroutineEventListener(listener:ListenerAdapter){
        listeners.add(listener)
    }

    override fun onEvent(event: GenericEvent) {

        if (event is SlashCommandEvent){
            GlobalScope.launch {
                SlashCommandListener.onSlashCommand(event)
            }
        } else if (event is MessageReceivedEvent){

        }
    }
}