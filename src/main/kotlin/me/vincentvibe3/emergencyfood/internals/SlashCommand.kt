package me.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface SlashCommand{

    //command to create
    val command: CommandData

    //handle events
    suspend fun handle(event: SlashCommandEvent)


}
