package me.vincentvibe3.emergencyfood.utils.events

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vincentvibe3.emergencyfood.utils.CoroutineListenerAdapter
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.SlashCommandManager
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object SlashCommandListener:CoroutineListenerAdapter<SlashCommandEvent> {

    override suspend fun onEvent(event: SlashCommandEvent) {
        Logging.logger.debug("SlashCommand ${event.name} called")
        retrieveCommand(event.name)?.handle(event)
    }

    //find the required command and run its handler function
    suspend fun onSlashCommand(event: SlashCommandEvent) {
        Logging.logger.debug("SlashCommand ${event.name} called")
        retrieveCommand(event.name)?.handle(event)
    }

    //find a register command from its name
    private fun retrieveCommand(name:String): SlashCommand?{
        return SlashCommandManager.getCommands()[name]
    }

}