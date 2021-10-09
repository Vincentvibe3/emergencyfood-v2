package me.vincentvibe3.emergencyfood.utils.events

import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.SlashCommandManager
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object SlashCommandListener: ListenerAdapter() {

    //find the required command and run its handler function
    override fun onSlashCommand(event: SlashCommandEvent) {
        retrieveCommand(event.name)?.handle(event)
    }

    //find a register command from its name
    private fun retrieveCommand(name:String): SlashCommand?{
        return SlashCommandManager.getCommands()[name]
    }

}