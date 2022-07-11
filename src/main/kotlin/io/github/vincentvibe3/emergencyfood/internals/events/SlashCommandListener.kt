package io.github.vincentvibe3.emergencyfood.internals.events

import io.github.vincentvibe3.emergencyfood.internals.CommandManager
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object SlashCommandListener : ListenerAdapter() {

    //find the required command and run its handler function
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        Logging.logger.debug("SlashCommand ${event.name} called")
        GlobalScope.launch {
            retrieveCommand(event.name)?.handle(event)
        }
    }

    //find a register command from its name
    private fun retrieveCommand(name: String): SlashCommand? {
        return CommandManager.getSlashCommands()[name]
    }

}