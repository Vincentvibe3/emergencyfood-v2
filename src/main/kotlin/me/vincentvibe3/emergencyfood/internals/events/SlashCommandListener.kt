package me.vincentvibe3.emergencyfood.internals.events

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.internals.SlashCommand
import me.vincentvibe3.emergencyfood.internals.CommandManager
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object SlashCommandListener: ListenerAdapter() {

    //find the required command and run its handler function
    override fun onSlashCommand(event: SlashCommandEvent) {
        Logging.logger.debug("SlashCommand ${event.name} called")
        GlobalScope.launch {
            retrieveCommand(event.name)?.handle(event)
        }
    }

    //find a register command from its name
    private fun retrieveCommand(name:String): SlashCommand?{
        return CommandManager.getSlashCommands()[name]
    }

}