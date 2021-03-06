package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface SlashCommand {

    //command to create
    val command: CommandData

    //handle events
    suspend fun handle(event: SlashCommandInteractionEvent)


}
