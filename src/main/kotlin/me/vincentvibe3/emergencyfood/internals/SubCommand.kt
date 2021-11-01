package me.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

interface SubCommand {

    val name: String

    val subCommand:SubcommandData

    suspend fun handle(event: SlashCommandEvent)

}