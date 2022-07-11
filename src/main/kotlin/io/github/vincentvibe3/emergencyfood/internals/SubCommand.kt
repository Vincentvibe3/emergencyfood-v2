package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

interface SubCommand {

    val subCommand: SubcommandData

    suspend fun handle(event: SlashCommandInteractionEvent)

}