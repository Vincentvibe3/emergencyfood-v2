package me.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class SlashCommand:GenericCommand() {

    //command to create
    abstract val command: CommandData

    //handle events
    abstract suspend fun handle(event: SlashCommandEvent)


}
