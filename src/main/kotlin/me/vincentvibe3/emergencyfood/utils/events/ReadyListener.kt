package me.vincentvibe3.emergencyfood.utils.events

import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object ReadyListener: ListenerAdapter() {

    // display info when bot is logged in
    override fun onReady(event: ReadyEvent){
        println("Logged in as ${event.jda.selfUser.name}#${event.jda.selfUser.discriminator}")
        println("Active in ${event.jda.guilds.size} guilds\n")

    }

}